package com.parcial.hospital.service;

import com.parcial.hospital.dto.MedicoForm;
import com.parcial.hospital.model.Consultorio;
import com.parcial.hospital.model.Medico;
import com.parcial.hospital.model.Role;
import com.parcial.hospital.model.RoleName;
import com.parcial.hospital.model.User;
import com.parcial.hospital.repository.ConsultorioRepository;
import com.parcial.hospital.repository.MedicoRepository;
import com.parcial.hospital.repository.RoleRepository;
import com.parcial.hospital.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {
    private final MedicoRepository medicoRepository;
    private final ConsultorioRepository consultorioRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoService(
            MedicoRepository medicoRepository,
            ConsultorioRepository consultorioRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.medicoRepository = medicoRepository;
        this.consultorioRepository = consultorioRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Medico> listarActivos() {
        return medicoRepository.findByActivoTrueOrderByUserApellidosAsc();
    }

    public Medico obtener(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Medico no encontrado"));
    }

    public Medico obtenerPorUsername(String username) {
        return medicoRepository.findByUserUsername(username)
                .orElseThrow(() -> new BusinessException("Medico no encontrado"));
    }

    @Transactional
    public Medico crear(MedicoForm form) {
        validarCampos(form);
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new BusinessException("El usuario ya existe");
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new BusinessException("El email ya existe");
        }
        if (medicoRepository.existsByNumeroLicencia(form.getNumeroLicencia())) {
            throw new BusinessException("La licencia medica ya existe");
        }

        Role role = roleRepository.findByNombre(RoleName.MEDICO)
                .orElseThrow(() -> new BusinessException("Rol MEDICO no existe"));
        Consultorio consultorio = obtenerConsultorio(form.getConsultorioId());

        User user = new User();
        user.setRole(role);
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEmail(form.getEmail());
        user.setNombres(form.getNombres());
        user.setApellidos(form.getApellidos());
        user.setTelefono(form.getTelefono());

        Medico medico = new Medico();
        medico.setUser(user);
        medico.setConsultorio(consultorio);
        medico.setNumeroLicencia(form.getNumeroLicencia());
        medico.setEspecialidad(form.getEspecialidad());
        medico.setHorarioInicio(form.getHorarioInicio());
        medico.setHorarioFin(form.getHorarioFin());
        validarHorario(medico);
        userRepository.save(user);
        return medicoRepository.save(medico);
    }

    @Transactional
    public Medico actualizar(Long id, MedicoForm form) {
        Medico medico = obtener(id);
        User user = medico.getUser();
        Consultorio consultorio = obtenerConsultorio(form.getConsultorioId());

        user.setNombres(form.getNombres());
        user.setApellidos(form.getApellidos());
        user.setTelefono(form.getTelefono());
        user.setEmail(form.getEmail());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        medico.setConsultorio(consultorio);
        medico.setEspecialidad(form.getEspecialidad());
        medico.setHorarioInicio(form.getHorarioInicio());
        medico.setHorarioFin(form.getHorarioFin());
        validarHorario(medico);
        return medicoRepository.save(medico);
    }

    @Transactional
    public void asignarConsultorio(Long medicoId, Long consultorioId) {
        Medico medico = obtener(medicoId);
        medico.setConsultorio(obtenerConsultorio(consultorioId));
        medicoRepository.save(medico);
    }

    @Transactional
    public void desactivar(Long id) {
        Medico medico = obtener(id);
        medico.setActivo(false);
        medico.getUser().setActivo(false);
        medicoRepository.save(medico);
    }

    public MedicoForm toForm(Medico medico) {
        MedicoForm form = new MedicoForm();
        form.setId(medico.getId());
        form.setUsername(medico.getUser().getUsername());
        form.setEmail(medico.getUser().getEmail());
        form.setNombres(medico.getUser().getNombres());
        form.setApellidos(medico.getUser().getApellidos());
        form.setTelefono(medico.getUser().getTelefono());
        form.setNumeroLicencia(medico.getNumeroLicencia());
        form.setEspecialidad(medico.getEspecialidad());
        form.setHorarioInicio(medico.getHorarioInicio());
        form.setHorarioFin(medico.getHorarioFin());
        if (medico.getConsultorio() != null) {
            form.setConsultorioId(medico.getConsultorio().getId());
        }
        return form;
    }

    private Consultorio obtenerConsultorio(Long consultorioId) {
        if (consultorioId == null) {
            return null;
        }
        return consultorioRepository.findById(consultorioId)
                .orElseThrow(() -> new BusinessException("Consultorio no encontrado"));
    }

    private void validarCampos(MedicoForm form) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new BusinessException("La contrasena es obligatoria");
        }
    }

    private void validarHorario(Medico medico) {
        if (medico.getHorarioInicio() == null || medico.getHorarioFin() == null
                || !medico.getHorarioInicio().isBefore(medico.getHorarioFin())) {
            throw new BusinessException("El horario del medico no es valido");
        }
    }
}
