package com.crm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Outbound;

@Repository
public interface OutboundRepository extends JpaRepository<Outbound, Long>{
  
  boolean existsById(Long id);
  
  Optional<Outbound> findById(Long id);
  /*
  @Query(value = "SELECT c FROM Consignment c WHERE c.merchant.id = :id")
  Page<Outbound> findByMerchantId(@Param("id") Long id, Pageable pageable);
  */
}
