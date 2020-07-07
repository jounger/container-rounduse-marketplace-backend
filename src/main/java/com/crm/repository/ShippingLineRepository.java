package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.ShippingLine;

@Repository
public interface ShippingLineRepository extends JpaRepository<ShippingLine, Long> {

  Boolean existsByCompanyCode(String name);

  Optional<ShippingLine> findByCompanyCode(String name);

}
