package com.parcial.hospital.config;

import com.parcial.hospital.model.Consultorio;
import com.parcial.hospital.model.Medico;
import com.parcial.hospital.model.Paciente;
import com.parcial.hospital.model.Role;
import com.parcial.hospital.model.RoleName;
import com.parcial.hospital.model.User;
import com.parcial.hospital.repository.ConsultorioRepository;
import com.parcial.hospital.repository.MedicoRepository;
import com.parcial.hospital.repository.PacienteRepository;
import com.parcial.hospital.repository.RoleRepository;
import com.parcial.hospital.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            ConsultorioRepository consultorioRepository,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                roleRepository.findByNombre(roleName).orElseGet(() -> {
                    Role role = new Role();
                    role.setNombre(roleName);
                    role.setDescripcion("Rol " + roleName.name());
                    return roleRepository.save(role);
                });
            }

            Consultorio consultorio = consultorioRepository.findAll().stream().findFirst().orElseGet(() -> {
                Consultorio nuevo = new Consultorio();
                nuevo.setCodigo("C-101");
                nuevo.setNombre("Consulta general");
                nuevo.setPiso(1);
                nuevo.setDescripcion("Consultorio base para atencion ambulatoria");
                return consultorioRepository.save(nuevo);
            });

            crearUsuario(userRepository, roleRepository, passwordEncoder, RoleName.ADMINISTRADOR,
                    "admin", "admin123", "admin@hospital.local", "Admin", "Principal", "3000000000");

            User medicoUser = crearUsuario(userRepository, roleRepository, passwordEncoder, RoleName.MEDICO,
                    "medico", "medico123", "medico@hospital.local", "Laura", "Gomez", "3010000000");
            if (medicoRepository.findByUserUsername("medico").isEmpty()) {
                Medico medico = new Medico();
                medico.setUser(medicoUser);
                medico.setConsultorio(consultorio);
                medico.setNumeroLicencia("MED-1001");
                medico.setEspecialidad("Medicina general");
                medico.setHorarioInicio(LocalTime.of(8, 0));
                medico.setHorarioFin(LocalTime.of(17, 0));
                medicoRepository.save(medico);
            }

            User pacienteUser = crearUsuario(userRepository, roleRepository, passwordEncoder, RoleName.PACIENTE,
                    "paciente", "paciente123", "paciente@hospital.local", "Carlos", "Perez", "3020000000");
            if (pacienteRepository.findByUserUsername("paciente").isEmpty()) {
                Paciente paciente = new Paciente();
                paciente.setUser(pacienteUser);
                paciente.setDocumentoIdentidad("CC100200300");
                paciente.setFechaNacimiento(LocalDate.of(1990, 5, 10));
                paciente.setDireccion("Direccion registrada");
                paciente.setTipoSangre("O+");
                paciente.setContactoEmergenciaNombre("Contacto");
                paciente.setContactoEmergenciaTelefono("3030000000");
                pacienteRepository.save(paciente);
            }
        };
    }

    private User crearUsuario(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            RoleName roleName,
            String username,
            String rawPassword,
            String email,
            String nombres,
            String apellidos,
            String telefono
    ) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            Role role = roleRepository.findByNombre(roleName).orElseThrow();
            User user = new User();
            user.setRole(role);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEmail(email);
            user.setNombres(nombres);
            user.setApellidos(apellidos);
            user.setTelefono(telefono);
            return userRepository.save(user);
        });
    }
}
