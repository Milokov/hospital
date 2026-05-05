package com.parcial.hospital.controller;

import com.parcial.hospital.dto.CitaForm;
import com.parcial.hospital.model.EstadoCita;
import com.parcial.hospital.model.Paciente;
import com.parcial.hospital.service.BusinessException;
import com.parcial.hospital.service.CitaService;
import com.parcial.hospital.service.ConsultorioService;
import com.parcial.hospital.service.MedicoService;
import com.parcial.hospital.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@PreAuthorize("hasRole('PACIENTE')")
public class PacienteController {
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final ConsultorioService consultorioService;
    private final CitaService citaService;

    public PacienteController(
            PacienteService pacienteService,
            MedicoService medicoService,
            ConsultorioService consultorioService,
            CitaService citaService
    ) {
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.consultorioService = consultorioService;
        this.citaService = citaService;
    }

    @Operation(summary = "Panel paciente con sus citas")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/paciente")
    public String panel(Model model, Principal principal) {
        Paciente paciente = pacienteService.obtenerPorUsername(principal.getName());
        model.addAttribute("paciente", paciente);
        model.addAttribute("citas", citaService.listarPorPaciente(paciente.getId()));
        return "paciente/panel";
    }

    @Operation(summary = "Formulario paciente para crear cita")
    @GetMapping("/paciente/citas/nueva")
    public String nuevaCita(Model model, Principal principal) {
        Paciente paciente = pacienteService.obtenerPorUsername(principal.getName());
        CitaForm form = new CitaForm();
        form.setPacienteId(paciente.getId());
        cargarDatosCita(model, form);
        return "paciente/cita-form";
    }

    @Operation(summary = "Crear cita por paciente")
    @PostMapping("/paciente/citas")
    public String crearCita(@ModelAttribute CitaForm citaForm, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Paciente paciente = pacienteService.obtenerPorUsername(principal.getName());
            citaForm.setPacienteId(paciente.getId());
            citaService.crear(citaForm);
            redirectAttributes.addFlashAttribute("success", "Cita solicitada correctamente");
            return "redirect:/paciente";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarDatosCita(model, citaForm);
            return "paciente/cita-form";
        }
    }

    @Operation(summary = "Formulario paciente para editar cita")
    @GetMapping("/paciente/citas/{id}/editar")
    public String editarCita(@PathVariable Long id, Model model) {
        CitaForm form = citaService.toForm(citaService.obtener(id));
        model.addAttribute("citaForm", form);
        model.addAttribute("estados", new EstadoCita[]{EstadoCita.CONFIRMADA, EstadoCita.CANCELADA});
        return "paciente/cita-edit";
    }

    @Operation(summary = "Actualizar cita por paciente")
    @PostMapping("/paciente/citas/{id}")
    public String actualizarCita(@PathVariable Long id, @ModelAttribute CitaForm citaForm, Principal principal, RedirectAttributes redirectAttributes) {
        citaService.actualizarPorPaciente(id, principal.getName(), citaForm);
        redirectAttributes.addFlashAttribute("success", "Cita actualizada");
        return "redirect:/paciente";
    }

    private void cargarDatosCita(Model model, CitaForm citaForm) {
        model.addAttribute("citaForm", citaForm);
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("consultorios", consultorioService.listarActivos());
    }
}
