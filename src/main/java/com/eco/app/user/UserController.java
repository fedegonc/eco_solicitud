package com.eco.app.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mensaje", "Bienvenido a la aplicación Eco Solicitud");
        return "index";
    }
    
    @RequestMapping("/user")
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Delegar la lógica de negocio al servicio
        if (!userService.prepareUserDashboard(session, model)) {
            return "redirect:/auth/login";
        }
        
        return "user/dashboard";
    }
}
