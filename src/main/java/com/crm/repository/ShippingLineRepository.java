package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.ShippingLine;

@Repository
public interface ShippingLineRepository extends JpaRepository<ShippingLine, Long>{
	
	Boolean existsByName(String name);
	
	Optional<ShippingLine> findByName(String name);
	
}
