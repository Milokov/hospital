package com.parcial.hospital.service;

import com.parcial.hospital.model.Paciente;
import com.parcial.hospital.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> listarActivos() {
        return pacienteRepository.findByActivoTrueOrderByUserApellidosAsc();
    }

    public Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Paciente no encontrado"));
    }

    public Paciente obtenerPorUsername(String username) {
        return pacienteRepository.findByUserUsername(username)
                .orElseThrow(() -> new BusinessException("Paciente no encontrado"));
    }
}
