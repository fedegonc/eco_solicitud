package com.eco.app.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * Obtiene todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
}
