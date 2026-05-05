package com.parcial.hospital.repository;

import com.parcial.hospital.model.Consultorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {
    List<Consultorio> findByActivoTrueOrderByCodigoAsc();

    boolean existsByCodigo(String codigo);
}
