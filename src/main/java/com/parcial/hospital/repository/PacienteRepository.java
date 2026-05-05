package com.parcial.hospital.repository;

import com.parcial.hospital.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByActivoTrueOrderByUserApellidosAsc();

    Optional<Paciente> findByUserUsername(String username);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}
