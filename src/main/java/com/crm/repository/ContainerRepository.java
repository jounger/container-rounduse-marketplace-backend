package com.crm.repository;

import com.crm.models.Container;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long>{

	Optional<Container> findByContainerNumber(String containerNumber);
	
	Boolean existsByContainerNumber(String containerNumber);
}
