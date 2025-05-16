package com.eco.app.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mensaje", "Bienvenido a la aplicación Eco Solicitud");
        return "index";
    }
    
    @RequestMapping("/user")
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Verificar si el usuario ha iniciado sesión
        User usuario = (User) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("usuario", usuario);
        return "user/dashboard";
    }
}
