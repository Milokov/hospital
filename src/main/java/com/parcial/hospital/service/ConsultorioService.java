package com.parcial.hospital.service;

import com.parcial.hospital.model.Consultorio;
import com.parcial.hospital.repository.ConsultorioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultorioService {
    private final ConsultorioRepository consultorioRepository;

    public ConsultorioService(ConsultorioRepository consultorioRepository) {
        this.consultorioRepository = consultorioRepository;
    }

    public List<Consultorio> listarActivos() {
        return consultorioRepository.findByActivoTrueOrderByCodigoAsc();
    }

    public Consultorio obtener(Long id) {
        return consultorioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Consultorio no encontrado"));
    }
}
