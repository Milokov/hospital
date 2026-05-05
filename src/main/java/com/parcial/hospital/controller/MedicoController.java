package com.parcial.hospital.controller;

import com.parcial.hospital.model.EstadoCita;
import com.parcial.hospital.model.Medico;
import com.parcial.hospital.service.CitaService;
import com.parcial.hospital.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@PreAuthorize("hasRole('MEDICO')")
public class MedicoController {
    private final MedicoService medicoService;
    private final CitaService citaService;

    public MedicoController(MedicoService medicoService, CitaService citaService) {
        this.medicoService = medicoService;
        this.citaService = citaService;
    }

    @Operation(summary = "Panel medico con sus consultas")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/medico")
    public String panel(Model model, Principal principal) {
        Medico medico = medicoService.obtenerPorUsername(principal.getName());
        model.addAttribute("medico", medico);
        model.addAttribute("citas", citaService.listarPorMedico(medico.getId()));
        model.addAttribute("estados", EstadoCita.values());
        return "medico/panel";
    }

    @Operation(summary = "Actualizar estado de cita por medico")
    @PostMapping("/medico/citas/{id}/estado")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoCita estado,
            @RequestParam(required = false) String observaciones,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        citaService.actualizarEstadoPorMedico(id, principal.getName(), estado, observaciones);
        redirectAttributes.addFlashAttribute("success", "Cita actualizada");
        return "redirect:/medico";
    }
}
