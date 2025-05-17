package com.eco.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("")
    public String adminHome(Model model) {
        model.addAttribute("mensaje", "Panel de Administración");
        return "admin/index";
    }
    
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, 
                               @RequestParam(required = false) Long editId,
                               @RequestParam(required = false) String orderBy,
                               @RequestParam(required = false, defaultValue = "false") boolean ascending,
                               @RequestParam(required = false, defaultValue = "0") int page,
                               @RequestParam(required = false, defaultValue = "10") int size,
                               @RequestParam(required = false, defaultValue = "dto") String viewType) {
        
        // Crear objeto Pageable para la paginación
        Pageable pageable = PageRequest.of(page, size);
        
        // Obtener los usuarios según el criterio de ordenación, paginación y tipo de vista
        if (orderBy != null) {
            model.addAttribute("orderBy", orderBy);
            model.addAttribute("ordenAscendente", ascending);
            
            if ("projection".equals(viewType)) {
                // Usar proyecciones para no exponer datos sensibles
                Page<UserProjection> pageUsuarios;
                
                switch (orderBy) {
                    case "createdAt":
                        pageUsuarios = adminService.getUsersOrderedByCreationDateProjection(ascending, pageable);
                        break;
                    case "username":
                        pageUsuarios = adminService.getUsersOrderedByUsernameProjection(ascending, pageable);
                        break;
                    case "loginCount":
                        pageUsuarios = adminService.getUsersOrderedByLoginCountProjection(ascending, pageable);
                        break;
                    default:
                        pageUsuarios = adminService.getAllUsersProjection(pageable);
                }
                
                prepareModelWithProjections(model, pageUsuarios, page, size);
            } else {
                // Usar DTOs por defecto para no exponer datos sensibles
                Page<UserDTO> pageUsuarios;
                
                switch (orderBy) {
                    case "createdAt":
                        pageUsuarios = adminService.getUsersOrderedByCreationDateDTO(ascending, pageable);
                        break;
                    case "username":
                        pageUsuarios = adminService.getUsersOrderedByUsernameDTO(ascending, pageable);
                        break;
                    case "loginCount":
                        pageUsuarios = adminService.getUsersOrderedByLoginCountDTO(ascending, pageable);
                        break;
                    default:
                        pageUsuarios = adminService.getAllUsersDTO(pageable);
                }
                
                prepareModelWithDTOs(model, pageUsuarios, page, size);
            }
        } else {
            if ("projection".equals(viewType)) {
                // Usar proyecciones para no exponer datos sensibles
                Page<UserProjection> pageUsuarios = adminService.getAllUsersProjection(pageable);
                prepareModelWithProjections(model, pageUsuarios, page, size);
            } else {
                // Usar DTOs por defecto para no exponer datos sensibles
                Page<UserDTO> pageUsuarios = adminService.getAllUsersDTO(pageable);
                prepareModelWithDTOs(model, pageUsuarios, page, size);
            }
        }
        
        model.addAttribute("mensaje", "Listado de Usuarios");
        model.addAttribute("viewType", viewType);
        
        // Mantener los parámetros de ordenación para la paginación
        if (orderBy != null) {
            model.addAttribute("orderBy", orderBy);
        }
        
        // Si se solicita editar un usuario, agregarlo al modelo
        if (editId != null) {
            User user = adminService.getUserById(editId);
            model.addAttribute("usuarioEditar", user);
        } else {
            // Si no se está editando, preparar un usuario nuevo para el formulario de creación
            model.addAttribute("usuarioNuevo", new User());
        }
        
        return "admin/usuarios";
    }
    
    /**
     * Prepara el modelo con una página de DTOs de usuario
     */
    private void prepareModelWithDTOs(Model model, Page<UserDTO> pageUsuarios, int page, int size) {
        model.addAttribute("pageUsuarios", pageUsuarios);
        model.addAttribute("usuarios", pageUsuarios.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageUsuarios.getTotalPages());
        model.addAttribute("totalItems", pageUsuarios.getTotalElements());
        model.addAttribute("size", size);
    }
    
    /**
     * Prepara el modelo con una página de proyecciones de usuario
     */
    private void prepareModelWithProjections(Model model, Page<UserProjection> pageUsuarios, int page, int size) {
        model.addAttribute("pageUsuarios", pageUsuarios);
        model.addAttribute("usuarios", pageUsuarios.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageUsuarios.getTotalPages());
        model.addAttribute("totalItems", pageUsuarios.getTotalElements());
        model.addAttribute("size", size);
    }
    
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(
            @ModelAttribute User usuario, 
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "deleteImage", required = false) Boolean deleteImage,
            RedirectAttributes redirectAttributes) {
        // La lógica de negocio ahora está completamente en el servicio
        AdminService.UserSaveResult result = adminService.saveUser(usuario, profileImage, deleteImage);
        
        // Usar el mensaje proporcionado por el servicio
        redirectAttributes.addFlashAttribute("mensaje", result.getMessage());
        
        return "redirect:/admin/usuarios";
    }
    
    // El método procesarImagen se ha movido al AdminService
    
    @GetMapping("/usuarios/eliminar/{id}")
    public String borrarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // La lógica de negocio y manejo de excepciones ahora está en el servicio y el advice
        adminService.deleteUser(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        return "redirect:/admin/usuarios";
    }
}
