package com.parcial.hospital.repository;

import com.parcial.hospital.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    List<Medico> findByActivoTrueOrderByUserApellidosAsc();

    Optional<Medico> findByUserUsername(String username);

    boolean existsByNumeroLicencia(String numeroLicencia);
}
