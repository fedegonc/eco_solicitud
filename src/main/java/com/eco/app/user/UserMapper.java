package com.eco.app.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para convertir entre User y UserDTO
 */
public class UserMapper {

    /**
     * Convierte una entidad User a un DTO
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .profileImageUrl(user.getProfileImageUrl())
                .loginCount(user.getLoginCount())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roleName(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }
    
    /**
     * Convierte una lista de entidades User a una lista de DTOs
     */
    public static List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return List.of();
        }
        
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte una página de entidades User a una página de DTOs
     */
    public static Page<UserDTO> toDTOPage(Page<User> userPage) {
        if (userPage == null) {
            return Page.empty();
        }
        
        List<UserDTO> dtos = userPage.getContent().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(dtos, userPage.getPageable(), userPage.getTotalElements());
    }
}
