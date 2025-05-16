package com.eco.app.user;

import com.eco.app.config.S3Service;
import com.eco.app.exception.ImageProcessingException;
import com.eco.app.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserService userService;
    private final S3Service s3Service;

    /**
     * Obtiene todos los usuarios del sistema
     */
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Obtiene un usuario por su ID
     */
    public User getUserById(Long id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Resultado de la operación de guardar usuario
     */
    public class UserSaveResult {
        private final User user;
        private final boolean isNew;
        
        public UserSaveResult(User user, boolean isNew) {
            this.user = user;
            this.isNew = isNew;
        }
        
        public User getUser() {
            return user;
        }
        
        public boolean isNew() {
            return isNew;
        }
        
        public String getMessage() {
            return isNew ? "Usuario creado correctamente" : "Usuario actualizado correctamente";
        }
    }
    
    /**
     * Crea un nuevo usuario o actualiza uno existente
     * @return Resultado que contiene el usuario guardado y si fue una creación o actualización
     */
    public UserSaveResult saveUser(User usuario, MultipartFile profileImage, Boolean deleteImage) {
        // Procesar imagen
        processUserImage(usuario, profileImage, deleteImage);
        
        // Guardar usuario
        boolean isNew = usuario.getId() == null;
        User savedUser;
        
        if (isNew) {
            savedUser = userService.registerUser(usuario);
        } else {
            // Si estamos editando un usuario y la contraseña está vacía, mantener la contraseña anterior
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                User existingUser = getUserById(usuario.getId());
                usuario.setPassword(existingUser.getPassword());
            }
            savedUser = userService.updateUser(usuario);
        }
        
        return new UserSaveResult(savedUser, isNew);
    }

    /**
     * Procesa la imagen de perfil del usuario
     */
    private void processUserImage(User usuario, MultipartFile profileImage, Boolean deleteImage) {
        try {
            // Subir nueva imagen
            if (profileImage != null && !profileImage.isEmpty()) {
                String imageUrl = s3Service.uploadFile(profileImage);
                usuario.setProfileImageUrl(imageUrl);
            }
            
            // Eliminar imagen existente
            if (Boolean.TRUE.equals(deleteImage) && usuario.getId() != null) {
                userService.getUserById(usuario.getId())
                    .filter(user -> user.getProfileImageUrl() != null)
                    .ifPresent(user -> {
                        try {
                            s3Service.deleteFile(user.getProfileImageUrl());
                        } catch (Exception e) {
                            log.error("Error al eliminar imagen: {}", e.getMessage());
                            throw new ImageProcessingException("Error al eliminar la imagen: " + e.getMessage());
                        }
                    });
                usuario.setProfileImageUrl(null);
            }
        } catch (Exception e) {
            log.error("Error al procesar imagen: {}", e.getMessage());
            throw new ImageProcessingException("Error al procesar la imagen: " + e.getMessage());
        }
    }

    /**
     * Elimina un usuario por su ID
     */
    public void deleteUser(Long id) {
        // Primero verificamos si el usuario existe
        User user = getUserById(id);
        
        // Si tiene imagen de perfil, la eliminamos
        if (user.getProfileImageUrl() != null) {
            try {
                s3Service.deleteFile(user.getProfileImageUrl());
            } catch (Exception e) {
                log.warn("No se pudo eliminar la imagen del usuario: {}", e.getMessage());
                // Continuamos con la eliminación del usuario aunque falle la imagen
            }
        }
        
        // Eliminamos el usuario
        userService.deleteUser(id);
    }
}
