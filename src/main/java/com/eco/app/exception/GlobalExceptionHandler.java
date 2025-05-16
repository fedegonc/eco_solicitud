package com.eco.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error("UserNotFoundException: {}", ex.getMessage());
        
        // Determinar si la solicitud viene del panel de administración
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/admin/")) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/usuarios";
        }
        
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/";
    }
    
    @ExceptionHandler(ImageProcessingException.class)
    public String handleImageProcessingException(ImageProcessingException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error("ImageProcessingException: {}", ex.getMessage());
        
        // Determinar si la solicitud viene del panel de administración
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/admin/")) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen: " + ex.getMessage());
            return "redirect:/admin/usuarios";
        }
        
        redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen: " + ex.getMessage());
        return "redirect:/";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        
        // Determinar si la solicitud viene del panel de administración
        String referer = request.getHeader("Referer");
        String requestURI = request.getRequestURI();
        
        if (requestURI.startsWith("/admin") || (referer != null && referer.contains("/admin/"))) {
            redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error: " + ex.getMessage());
            return "redirect:/admin";
        }
        
        redirectAttributes.addFlashAttribute("error", "Ha ocurrido un error: " + ex.getMessage());
        return "redirect:/";
    }
}
