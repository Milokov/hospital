package com.parcial.hospital.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/")
    public String home() {
        return redirectByRole();
    }

    @Operation(summary = "Formulario de login")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isRealAuthenticatedUser(authentication)) {
            return redirectByRole();
        }
        return "login";
    }

    @Operation(summary = "Pagina de acceso denegado")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/403")
    public String forbidden() {
        return "errors/403";
    }

    private String redirectByRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isRealAuthenticatedUser(authentication)) {
            return "redirect:/login";
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
            return "redirect:/admin";
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"))) {
            return "redirect:/medico";
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
            return "redirect:/paciente";
        }
        return "redirect:/login";
    }

    private boolean isRealAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
