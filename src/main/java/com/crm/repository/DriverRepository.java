package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>{

	Boolean deleteByUsername(String username);
	
	Boolean existsByUsername(String username);
	
	Optional<Driver> findByUsername(String username);
}
