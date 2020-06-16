package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.enums.EnumPermission;
import com.crm.models.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Optional<Permission> findByName(EnumPermission name);

	Boolean existsByName(EnumPermission name);
}
