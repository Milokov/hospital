package com.parcial.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 30)
    private String documentoIdentidad;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 150)
    private String direccion;

    @Column(length = 5)
    private String tipoSangre;

    @Column(length = 120)
    private String contactoEmergenciaNombre;

    @Column(length = 20)
    private String contactoEmergenciaTelefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "paciente")
    private List<Cita> citas = new ArrayList<>();
}
