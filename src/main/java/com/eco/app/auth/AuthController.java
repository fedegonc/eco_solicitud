package com.eco.app.auth;

import com.eco.app.user.Role;
import com.eco.app.user.User;
import com.eco.app.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            // Intentar autenticar al usuario usando el servicio
            Optional<User> userOpt = userService.authenticateUser(username, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Guardar el usuario en la sesión
                session.setAttribute("usuario", user);
                
                // Si el usuario es administrador, establecer la bandera isAdmin
                if (Role.ADMIN.equals(user.getRole())) {
                    session.setAttribute("isAdmin", true);
                    return "redirect:/admin";
                } else {
                    return "redirect:/user/dashboard";
                }
            } else {
                model.addAttribute("error", "Usuario o contraseña incorrectos");
                return "auth/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al iniciar sesión: " + e.getMessage());
            return "auth/login";
        }
    }
    
    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          Model model) {
        // Validación básica
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "El nombre de usuario es obligatorio");
            return "auth/register";
        }
        
        if (password == null || password.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "auth/register";
        }
        
        try {
            // Registrar al usuario usando el servicio
            User user = userService.registerUser(username, password);
            // En una aplicación real, la contraseña debería estar encriptada
            
            // Redirigir al login con un mensaje de éxito
            model.addAttribute("mensaje", "Registro exitoso. Por favor inicia sesión.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "auth/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
