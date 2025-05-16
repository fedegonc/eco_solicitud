package com.eco.app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public String listarUsuarios(Model model, @RequestParam(required = false) Long editId) {
        // Obtener todos los usuarios de la base de datos
        List<User> usuarios = adminService.getAllUsers();
        
        // Agregar la lista de usuarios al modelo
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("mensaje", "Listado de Usuarios");
        
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
