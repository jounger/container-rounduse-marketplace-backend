package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Operator;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long>{
	
}
