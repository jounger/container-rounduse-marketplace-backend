package com.crm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crm.models.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

  Optional<Supplier> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean existsByPhone(String phone);
  
  Boolean existsByCompanyCode(String companyCode);

  Page<Supplier> findByStatus(String status, Pageable pageable);
  
  @Query(value = "FROM Supplier s LEFT JOIN s.roles r WHERE r.name in ('ROLE_MERCHANT','ROLE_FORWARER')")
  Page<Supplier> findByRole(Pageable pageable);
}
