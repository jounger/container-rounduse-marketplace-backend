package com.crm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Consignment;

@Repository
public interface ConsignmentRepository extends JpaRepository<Consignment, Long>{
  
  boolean existsById(Long id);
  
  Optional<Consignment> findById(Long id);
  
  @Query(value = "SELECT c FROM Consignment c WHERE c.merchant.id = :id")
  Page<Consignment> findByMerchantId(@Param("id") Long id, Pageable pageable);
}
