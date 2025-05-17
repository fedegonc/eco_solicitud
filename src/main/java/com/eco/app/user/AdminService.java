package com.eco.app.user;

import com.eco.app.config.S3Service;
import com.eco.app.exception.ImageProcessingException;
import com.eco.app.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Obtiene todos los usuarios del sistema con paginación
     * @param pageable Información de paginación
     * @return Página de usuarios
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }
    
    /**
     * Obtiene todos los usuarios del sistema con paginación usando DTO
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario
     */
    public Page<UserDTO> getAllUsersDTO(Pageable pageable) {
        return userService.getAllUsersDTO(pageable);
    }
    
    /**
     * Obtiene todos los usuarios del sistema con paginación usando Projection
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario
     */
    public Page<UserProjection> getAllUsersProjection(Pageable pageable) {
        return userService.getAllUsersProjection(pageable);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por fecha de creación
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     */
    public List<User> getUsersOrderedByCreationDate(boolean ascending) {
        List<User> users = userService.getAllUsers();
        
        if (ascending) {
            users.sort((u1, u2) -> u1.getCreatedAt().compareTo(u2.getCreatedAt()));
        } else {
            users.sort((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()));
        }
        
        return users;
    }
    
    /**
     * Obtiene todos los usuarios ordenados por fecha de creación con paginación
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de usuarios ordenados por fecha de creación
     */
    public Page<User> getUsersOrderedByCreationDate(boolean ascending, Pageable pageable) {
        return userService.getUsersOrderedByCreationDate(ascending, pageable);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por fecha de creación con paginación usando DTO
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario ordenados por fecha de creación
     */
    public Page<UserDTO> getUsersOrderedByCreationDateDTO(boolean ascending, Pageable pageable) {
        return userService.getUsersOrderedByCreationDateDTO(ascending, pageable);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por fecha de creación con paginación usando Projection
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario ordenados por fecha de creación
     */
    public Page<UserProjection> getUsersOrderedByCreationDateProjection(boolean ascending, Pageable pageable) {
        return userService.getUsersOrderedByCreationDateProjection(ascending, pageable);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por nombre de usuario con paginación usando DTO
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario ordenados por nombre de usuario
     */
    public Page<UserDTO> getUsersOrderedByUsernameDTO(boolean ascending, Pageable pageable) {
        return userService.getUsersOrderedByUsernameDTO(ascending, pageable);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por nombre de usuario con paginación usando Projection
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario ordenados por nombre de usuario
     */
    public Page<UserProjection> getUsersOrderedByUsernameProjection(boolean ascending, Pageable pageable) {
        return userService.getUsersOrderedByUsernameProjection(ascending, pageable);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por cantidad de inicios de sesión con paginación usando DTO
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario ordenados por cantidad de inicios de sesión
     */
    public Page<UserDTO> getUsersOrderedByLoginCountDTO(boolean ascending, Pageable pageable) {
        return userService.getUsersOrderedByLoginCountDTO(ascending, pageable);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por cantidad de inicios de sesión con paginación usando Projection
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario ordenados por cantidad de inicios de sesión
     */
    public Page<UserProjection> getUsersOrderedByLoginCountProjection(boolean ascending, Pageable pageable) {
        return userService.getUsersOrderedByLoginCountProjection(ascending, pageable);
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
