package com.parcial.hospital.controller;

import com.parcial.hospital.dto.CitaForm;
import com.parcial.hospital.dto.MedicoForm;
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

@Controller
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {
    private final MedicoService medicoService;
    private final ConsultorioService consultorioService;
    private final PacienteService pacienteService;
    private final CitaService citaService;

    public AdminController(
            MedicoService medicoService,
            ConsultorioService consultorioService,
            PacienteService pacienteService,
            CitaService citaService
    ) {
        this.medicoService = medicoService;
        this.consultorioService = consultorioService;
        this.pacienteService = pacienteService;
        this.citaService = citaService;
    }

    @Operation(summary = "Panel administrador")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/admin")
    public String panel(Model model) {
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("consultorios", consultorioService.listarActivos());
        return "admin/panel";
    }

    @Operation(summary = "Formulario para crear medico")
    @GetMapping("/admin/medicos/nuevo")
    public String nuevoMedico(Model model) {
        model.addAttribute("medicoForm", new MedicoForm());
        model.addAttribute("consultorios", consultorioService.listarActivos());
        return "admin/medico-form";
    }

    @Operation(summary = "Crear medico")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/admin/medicos")
    public String crearMedico(@ModelAttribute MedicoForm medicoForm, Model model, RedirectAttributes redirectAttributes) {
        try {
            medicoService.crear(medicoForm);
            redirectAttributes.addFlashAttribute("success", "Medico creado correctamente");
            return "redirect:/admin";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("consultorios", consultorioService.listarActivos());
            return "admin/medico-form";
        }
    }

    @Operation(summary = "Formulario para editar medico")
    @GetMapping("/admin/medicos/{id}/editar")
    public String editarMedico(@PathVariable Long id, Model model) {
        model.addAttribute("medicoForm", medicoService.toForm(medicoService.obtener(id)));
        model.addAttribute("consultorios", consultorioService.listarActivos());
        return "admin/medico-form";
    }

    @Operation(summary = "Actualizar medico")
    @PostMapping("/admin/medicos/{id}")
    public String actualizarMedico(@PathVariable Long id, @ModelAttribute MedicoForm medicoForm, Model model, RedirectAttributes redirectAttributes) {
        try {
            medicoService.actualizar(id, medicoForm);
            redirectAttributes.addFlashAttribute("success", "Medico actualizado correctamente");
            return "redirect:/admin";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("consultorios", consultorioService.listarActivos());
            return "admin/medico-form";
        }
    }

    @Operation(summary = "Desactivar medico")
    @PostMapping("/admin/medicos/{id}/desactivar")
    public String desactivarMedico(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        medicoService.desactivar(id);
        redirectAttributes.addFlashAttribute("success", "Medico desactivado");
        return "redirect:/admin";
    }

    @Operation(summary = "Formulario para crear cita")
    @GetMapping("/admin/citas/nueva")
    public String nuevaCita(Model model) {
        cargarDatosCita(model, new CitaForm());
        return "admin/cita-form";
    }

    @Operation(summary = "Crear cita")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/admin/citas")
    public String crearCita(@ModelAttribute CitaForm citaForm, Model model, RedirectAttributes redirectAttributes) {
        try {
            citaService.crear(citaForm);
            redirectAttributes.addFlashAttribute("success", "Cita creada correctamente");
            return "redirect:/admin";
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarDatosCita(model, citaForm);
            return "admin/cita-form";
        }
    }

    private void cargarDatosCita(Model model, CitaForm citaForm) {
        model.addAttribute("citaForm", citaForm);
        model.addAttribute("medicos", medicoService.listarActivos());
        model.addAttribute("pacientes", pacienteService.listarActivos());
        model.addAttribute("consultorios", consultorioService.listarActivos());
    }
}
