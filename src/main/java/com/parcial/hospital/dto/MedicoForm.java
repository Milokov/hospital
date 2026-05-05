package com.parcial.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class MedicoForm {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String numeroLicencia;
    private String especialidad;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private Long consultorioId;
}
