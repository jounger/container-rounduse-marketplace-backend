package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.enums.EnumRole;
import com.crm.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(EnumRole name);
  
  Optional<Role> findByName(String name);
  
  Boolean existsByName(EnumRole name);
  
  Boolean existsByName(String name);
  
//  @Query(value = "FROM role r WHERE r.name LIKE '%:name%'", nativeQuery = true)
//  Role findByName(@Param("name") String name);
  
}
