package com.parcial.hospital.repository;

import com.parcial.hospital.model.Cita;
import com.parcial.hospital.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByMedicoIdOrderByFechaDescHoraInicioDesc(Long medicoId);

    List<Cita> findByPacienteIdOrderByFechaDescHoraInicioDesc(Long pacienteId);

    boolean existsByMedicoIdAndFechaAndHoraInicioAndEstadoNot(Long medicoId, LocalDate fecha, LocalTime horaInicio, EstadoCita estado);

    boolean existsByPacienteIdAndFechaAndHoraInicioAndEstadoNot(Long pacienteId, LocalDate fecha, LocalTime horaInicio, EstadoCita estado);

    boolean existsByConsultorioIdAndFechaAndHoraInicioAndEstadoNot(Long consultorioId, LocalDate fecha, LocalTime horaInicio, EstadoCita estado);
}
