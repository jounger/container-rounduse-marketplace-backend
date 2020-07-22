package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Operator;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long>{

  Optional<Operator> findByUsername(String username);
	
}
