package com.eco.app.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Registra un nuevo usuario con rol USER por defecto
     */
    public User registerUser(String username, String password) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        
        // Crear un nuevo usuario con rol USER
        User user = new User(username, password);
        user.setRole(Role.USER);
        
        // Guardar el usuario en la base de datos
        return userRepository.save(user);
    }
    
    /**
     * Registra un nuevo usuario completo
     */
    public User registerUser(User user) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        
        // Asegurarse de que tenga un rol
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        
        // Guardar el usuario en la base de datos
        return userRepository.save(user);
    }
    
    /**
     * Autentica a un usuario
     */
    public Optional<User> authenticateUser(String username, String password) {
        // Buscar el usuario por nombre de usuario
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        // Verificar si el usuario existe y la contraseña es correcta
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        
        return Optional.empty();
    }
    
    /**
     * Actualiza los campos de seguimiento de inicio de sesión de un usuario
     * @param user El usuario que ha iniciado sesión
     * @return El usuario actualizado
     */
    public User updateLoginStats(User user) {
        // Actualizar la última hora de inicio de sesión
        user.setLastLoginAt(LocalDateTime.now());
        
        // Incrementar el contador de inicios de sesión
        if (user.getLoginCount() == null) {
            user.setLoginCount(1);
        } else {
            user.setLoginCount(user.getLoginCount() + 1);
        }
        
        // Guardar los cambios
        return userRepository.save(user);
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Obtiene todos los usuarios con paginación
     * @param pageable Información de paginación
     * @return Página de usuarios
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * Obtiene todos los usuarios con paginación usando DTO para no exponer datos sensibles
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario
     */
    public Page<UserDTO> getAllUsersDTO(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return UserMapper.toDTOPage(userPage);
    }
    
    /**
     * Obtiene todos los usuarios con paginación usando Projection para no exponer datos sensibles
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario
     */
    public Page<UserProjection> getAllUsersProjection(Pageable pageable) {
        return userRepository.findAllProjectedBy(pageable, UserProjection.class);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por fecha de creación con paginación
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de usuarios ordenados por fecha de creación
     */
    public Page<User> getUsersOrderedByCreationDate(boolean ascending, Pageable pageable) {
        if (ascending) {
            return userRepository.findAllByOrderByCreatedAtAsc(pageable);
        } else {
            return userRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
    }
    
    /**
     * Obtiene todos los usuarios ordenados por fecha de creación con paginación usando DTO
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario ordenados por fecha de creación
     */
    public Page<UserDTO> getUsersOrderedByCreationDateDTO(boolean ascending, Pageable pageable) {
        Page<User> userPage;
        if (ascending) {
            userPage = userRepository.findAllByOrderByCreatedAtAsc(pageable);
        } else {
            userPage = userRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return UserMapper.toDTOPage(userPage);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por fecha de creación con paginación usando Projection
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario ordenados por fecha de creación
     */
    public Page<UserProjection> getUsersOrderedByCreationDateProjection(boolean ascending, Pageable pageable) {
        if (ascending) {
            return userRepository.findAllByOrderByCreatedAtAscProjected(pageable, UserProjection.class);
        } else {
            return userRepository.findAllByOrderByCreatedAtDescProjected(pageable, UserProjection.class);
        }
    }
    
    /**
     * Obtiene todos los usuarios ordenados por nombre de usuario con paginación
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de usuarios ordenados por nombre de usuario
     */
    public Page<User> getUsersOrderedByUsername(boolean ascending, Pageable pageable) {
        if (ascending) {
            return userRepository.findAllByOrderByUsernameAsc(pageable);
        } else {
            return userRepository.findAllByOrderByUsernameDesc(pageable);
        }
    }
    
    /**
     * Obtiene todos los usuarios ordenados por nombre de usuario con paginación usando DTO
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario ordenados por nombre de usuario
     */
    public Page<UserDTO> getUsersOrderedByUsernameDTO(boolean ascending, Pageable pageable) {
        Page<User> userPage;
        if (ascending) {
            userPage = userRepository.findAllByOrderByUsernameAsc(pageable);
        } else {
            userPage = userRepository.findAllByOrderByUsernameDesc(pageable);
        }
        return UserMapper.toDTOPage(userPage);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por nombre de usuario con paginación usando Projection
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario ordenados por nombre de usuario
     */
    public Page<UserProjection> getUsersOrderedByUsernameProjection(boolean ascending, Pageable pageable) {
        if (ascending) {
            return userRepository.findAllByOrderByUsernameAscProjected(pageable, UserProjection.class);
        } else {
            return userRepository.findAllByOrderByUsernameDescProjected(pageable, UserProjection.class);
        }
    }
    
    /**
     * Obtiene todos los usuarios ordenados por cantidad de inicios de sesión con paginación
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de usuarios ordenados por cantidad de inicios de sesión
     */
    public Page<User> getUsersOrderedByLoginCount(boolean ascending, Pageable pageable) {
        if (ascending) {
            return userRepository.findAllByOrderByLoginCountAsc(pageable);
        } else {
            return userRepository.findAllByOrderByLoginCountDesc(pageable);
        }
    }
    
    /**
     * Obtiene todos los usuarios ordenados por cantidad de inicios de sesión con paginación usando DTO
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de DTOs de usuario ordenados por cantidad de inicios de sesión
     */
    public Page<UserDTO> getUsersOrderedByLoginCountDTO(boolean ascending, Pageable pageable) {
        Page<User> userPage;
        if (ascending) {
            userPage = userRepository.findAllByOrderByLoginCountAsc(pageable);
        } else {
            userPage = userRepository.findAllByOrderByLoginCountDesc(pageable);
        }
        return UserMapper.toDTOPage(userPage);
    }
    
    /**
     * Obtiene todos los usuarios ordenados por cantidad de inicios de sesión con paginación usando Projection
     * @param ascending true para ordenar de forma ascendente, false para ordenar de forma descendente
     * @param pageable Información de paginación
     * @return Página de proyecciones de usuario ordenados por cantidad de inicios de sesión
     */
    public Page<UserProjection> getUsersOrderedByLoginCountProjection(boolean ascending, Pageable pageable) {
        if (ascending) {
            return userRepository.findAllByOrderByLoginCountAscProjected(pageable, UserProjection.class);
        } else {
            return userRepository.findAllByOrderByLoginCountDescProjected(pageable, UserProjection.class);
        }
    }
    
    /**
     * Obtiene un usuario por ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Obtiene un usuario por nombre de usuario
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Actualiza un usuario existente
     */
    public User updateUser(User user) {
        // Verificar si el usuario existe
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        // Guardar los cambios
        return userRepository.save(user);
    }
    
    /**
     * Elimina un usuario
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Verifica si un usuario está autenticado en la sesión
     * @param session La sesión HTTP actual
     * @return El usuario autenticado o null si no hay usuario autenticado
     */
    public User getAuthenticatedUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
    
    /**
     * Verifica si un usuario está autenticado y prepara el modelo para la vista del dashboard
     * @param session La sesión HTTP actual
     * @param model El modelo para la vista
     * @return true si el usuario está autenticado, false en caso contrario
     */
    public boolean prepareUserDashboard(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return false;
        }
        
        model.addAttribute("user", user);
        return true;
    }
}
