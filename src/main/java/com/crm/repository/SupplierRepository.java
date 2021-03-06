package com.crm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

  Optional<Supplier> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean existsByPhone(String phone);

  Boolean existsByCompanyCode(String companyCode);

  Boolean existsByTin(String tin);

  Boolean existsByFax(String fax);

  Page<Supplier> findByStatus(String status, Pageable pageable);

  @Query(value = "SELECT s FROM Supplier s LEFT JOIN s.roles r WHERE r.name IN :roles")
  Page<Supplier> findByRole(@Param("roles") List<String> roles, Pageable pageable);
}
