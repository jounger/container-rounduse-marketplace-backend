package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Icd;

@Repository
public interface IcdRepository extends JpaRepository<Icd, Long>{
	
	Optional<Icd> findByNameCode(String nameCode);
	
	Boolean existsByNameCode(String nameCode);
}
