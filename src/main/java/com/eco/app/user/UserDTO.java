package com.eco.app.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferir información de usuario sin exponer datos sensibles
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    private String username;
    private String nombre;
    private String apellido;
    // No incluimos password ni email por seguridad
    private String profileImageUrl;
    private Integer loginCount;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Solo incluimos el nombre del rol, no el objeto completo
    private String roleName;
}
