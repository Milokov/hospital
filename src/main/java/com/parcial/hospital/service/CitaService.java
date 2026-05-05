package com.parcial.hospital.service;

import com.parcial.hospital.dto.CitaForm;
import com.parcial.hospital.model.Cita;
import com.parcial.hospital.model.Consultorio;
import com.parcial.hospital.model.EstadoCita;
import com.parcial.hospital.model.Medico;
import com.parcial.hospital.model.Paciente;
import com.parcial.hospital.repository.CitaRepository;
import com.parcial.hospital.repository.ConsultorioRepository;
import com.parcial.hospital.repository.MedicoRepository;
import com.parcial.hospital.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CitaService {
    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final ConsultorioRepository consultorioRepository;

    public CitaService(
            CitaRepository citaRepository,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository,
            ConsultorioRepository consultorioRepository
    ) {
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.consultorioRepository = consultorioRepository;
    }

    public List<Cita> listarPorMedico(Long medicoId) {
        return citaRepository.findByMedicoIdOrderByFechaDescHoraInicioDesc(medicoId);
    }

    public List<Cita> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteIdOrderByFechaDescHoraInicioDesc(pacienteId);
    }

    public Cita obtener(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cita no encontrada"));
    }

    @Transactional
    public Cita crear(CitaForm form) {
        Medico medico = medicoRepository.findById(form.getMedicoId())
                .orElseThrow(() -> new BusinessException("Medico no encontrado"));
        Paciente paciente = pacienteRepository.findById(form.getPacienteId())
                .orElseThrow(() -> new BusinessException("Paciente no encontrado"));
        Consultorio consultorio = form.getConsultorioId() != null
                ? consultorioRepository.findById(form.getConsultorioId()).orElseThrow(() -> new BusinessException("Consultorio no encontrado"))
                : medico.getConsultorio();

        if (consultorio == null) {
            throw new BusinessException("El medico no tiene consultorio asignado");
        }

        Cita cita = new Cita();
        cita.setMedico(medico);
        cita.setPaciente(paciente);
        cita.setConsultorio(consultorio);
        cita.setFecha(form.getFecha());
        cita.setHoraInicio(form.getHoraInicio());
        cita.setHoraFin(form.getHoraFin());
        cita.setMotivo(form.getMotivo());
        cita.setObservaciones(form.getObservaciones());
        cita.setEstado(form.getEstado() == null ? EstadoCita.PROGRAMADA : form.getEstado());
        validarCita(cita);
        validarDisponibilidad(cita);
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita actualizarPorPaciente(Long citaId, String username, CitaForm form) {
        Cita cita = obtener(citaId);
        if (!cita.getPaciente().getUser().getUsername().equals(username)) {
            throw new BusinessException("No puedes modificar citas de otro paciente");
        }
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new BusinessException("Una cita atendida no puede modificarse");
        }
        cita.setMotivo(form.getMotivo());
        cita.setObservaciones(form.getObservaciones());
        if (form.getEstado() == EstadoCita.CANCELADA || form.getEstado() == EstadoCita.CONFIRMADA) {
            cita.setEstado(form.getEstado());
        }
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita actualizarEstadoPorMedico(Long citaId, String username, EstadoCita estado, String observaciones) {
        Cita cita = obtener(citaId);
        if (!cita.getMedico().getUser().getUsername().equals(username)) {
            throw new BusinessException("No puedes modificar citas de otro medico");
        }
        cita.setEstado(estado);
        cita.setObservaciones(observaciones);
        return citaRepository.save(cita);
    }

    public CitaForm toForm(Cita cita) {
        CitaForm form = new CitaForm();
        form.setId(cita.getId());
        form.setMedicoId(cita.getMedico().getId());
        form.setPacienteId(cita.getPaciente().getId());
        form.setConsultorioId(cita.getConsultorio().getId());
        form.setFecha(cita.getFecha());
        form.setHoraInicio(cita.getHoraInicio());
        form.setHoraFin(cita.getHoraFin());
        form.setEstado(cita.getEstado());
        form.setMotivo(cita.getMotivo());
        form.setObservaciones(cita.getObservaciones());
        return form;
    }

    private void validarCita(Cita cita) {
        if (cita.getFecha() == null || cita.getFecha().isBefore(LocalDate.now())) {
            throw new BusinessException("La fecha de la cita no puede ser anterior a hoy");
        }
        if (cita.getHoraInicio() == null || cita.getHoraFin() == null || !cita.getHoraInicio().isBefore(cita.getHoraFin())) {
            throw new BusinessException("La hora de la cita no es valida");
        }
        if (cita.getHoraInicio().isBefore(cita.getMedico().getHorarioInicio())
                || cita.getHoraFin().isAfter(cita.getMedico().getHorarioFin())) {
            throw new BusinessException("La cita esta fuera del horario del medico");
        }
    }

    private void validarDisponibilidad(Cita cita) {
        if (citaRepository.existsByMedicoIdAndFechaAndHoraInicioAndEstadoNot(cita.getMedico().getId(), cita.getFecha(), cita.getHoraInicio(), EstadoCita.CANCELADA)) {
            throw new BusinessException("El medico ya tiene una cita en ese horario");
        }
        if (citaRepository.existsByPacienteIdAndFechaAndHoraInicioAndEstadoNot(cita.getPaciente().getId(), cita.getFecha(), cita.getHoraInicio(), EstadoCita.CANCELADA)) {
            throw new BusinessException("El paciente ya tiene una cita en ese horario");
        }
        if (citaRepository.existsByConsultorioIdAndFechaAndHoraInicioAndEstadoNot(cita.getConsultorio().getId(), cita.getFecha(), cita.getHoraInicio(), EstadoCita.CANCELADA)) {
            throw new BusinessException("El consultorio ya esta ocupado en ese horario");
        }
    }
}
