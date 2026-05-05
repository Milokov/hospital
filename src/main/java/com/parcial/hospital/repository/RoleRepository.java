package com.parcial.hospital.repository;

import com.parcial.hospital.model.Role;
import com.parcial.hospital.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombre(RoleName nombre);
}
