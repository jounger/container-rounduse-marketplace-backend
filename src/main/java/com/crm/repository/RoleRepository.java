package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.enums.EnumRole;
import com.crm.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(EnumRole name);
  
  Boolean existsByName(EnumRole name);
  
  @Query(value = "FROM role r WHERE r.name LIKE '%:name%'", nativeQuery = true)
  Role findByName(@Param("name") String name);
  
}
