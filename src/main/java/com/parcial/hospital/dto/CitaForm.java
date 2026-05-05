package com.parcial.hospital.dto;

import com.parcial.hospital.model.EstadoCita;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CitaForm {
    private Long id;
    private Long medicoId;
    private Long pacienteId;
    private Long consultorioId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoCita estado;
    private String motivo;
    private String observaciones;
}
