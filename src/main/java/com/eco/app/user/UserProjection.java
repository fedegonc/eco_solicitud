package com.eco.app.user;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;

/**
 * Projection para consultas específicas de usuario sin exponer datos sensibles
 */
public interface UserProjection {
    
    Long getId();
    String getUsername();
    String getNombre();
    String getApellido();
    String getProfileImageUrl();
    Integer getLoginCount();
    LocalDateTime getLastLoginAt();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    
    // Método para obtener solo el nombre del rol
    @Value("#{target.role != null ? target.role.name() : null}")
    String getRoleName();
}
