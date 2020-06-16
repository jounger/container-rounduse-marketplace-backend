package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Port;

@Repository
public interface PortRepository extends JpaRepository<Port, Long>{

	Optional<Port> findByName(String name);
	
	Boolean existsByName(String name);
	
	Boolean existsByNameCode(String nameCode);
}
