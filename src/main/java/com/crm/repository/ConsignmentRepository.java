package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Consignment;

@Repository
public interface ConsignmentRepository extends JpaRepository<Consignment, Long>{
  
  Optional<Consignment> findById(Long id);
}
