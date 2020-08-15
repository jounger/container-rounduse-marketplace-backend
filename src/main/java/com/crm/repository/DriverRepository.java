package com.crm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>{

	Boolean deleteByUsername(String username);
	
	Boolean existsByUsername(String username);
	
	Boolean existsByDriverLicense(String driverLicense);
	
	Optional<Driver> findByUsername(String username);
	
	@Query(value = "FROM Driver d WHERE d.forwarder.username = :username")
	Page<Driver> findByForwarder(@Param("username") String username, Pageable pageable);
}
