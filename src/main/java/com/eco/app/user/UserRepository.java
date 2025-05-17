package com.eco.app.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    // Métodos originales para paginación
    Page<User> findAll(Pageable pageable);
    Page<User> findAllByOrderByCreatedAtAsc(Pageable pageable);
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<User> findAllByOrderByUsernameAsc(Pageable pageable);
    Page<User> findAllByOrderByUsernameDesc(Pageable pageable);
    Page<User> findAllByOrderByLoginCountAsc(Pageable pageable);
    Page<User> findAllByOrderByLoginCountDesc(Pageable pageable);
    
    // Consulta personalizada para obtener usuarios con proyección
    @Query("SELECT u FROM User u")
    <T> List<T> findAllProjectedBy(Class<T> type);
    
    // Método para obtener todos los usuarios con proyección y paginación
    @Query("SELECT u FROM User u")
    <T> Page<T> findAllProjectedBy(Pageable pageable, Class<T> type);
    
    // Métodos para paginación con ordenamiento y proyección
    @Query("SELECT u FROM User u ORDER BY u.createdAt ASC")
    <T> Page<T> findAllByOrderByCreatedAtAscProjected(Pageable pageable, Class<T> type);
    
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    <T> Page<T> findAllByOrderByCreatedAtDescProjected(Pageable pageable, Class<T> type);
    
    // Métodos para ordenar por nombre de usuario
    @Query("SELECT u FROM User u ORDER BY u.username ASC")
    <T> Page<T> findAllByOrderByUsernameAscProjected(Pageable pageable, Class<T> type);
    
    @Query("SELECT u FROM User u ORDER BY u.username DESC")
    <T> Page<T> findAllByOrderByUsernameDescProjected(Pageable pageable, Class<T> type);
    
    // Métodos para ordenar por cantidad de inicios de sesión
    @Query("SELECT u FROM User u ORDER BY u.loginCount ASC NULLS FIRST")
    <T> Page<T> findAllByOrderByLoginCountAscProjected(Pageable pageable, Class<T> type);
    
    @Query("SELECT u FROM User u ORDER BY u.loginCount DESC NULLS LAST")
    <T> Page<T> findAllByOrderByLoginCountDescProjected(Pageable pageable, Class<T> type);
}
