package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

  @Query(value = "FROM Payment p WHERE p.sender.username = :username OR p.recipient.username = :username")
  Page<Payment> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM Payment p WHERE p.contract.id = :id")
  Page<Payment> findByContract(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM Payment p WHERE p.contract.id = :id AND (p.sender.id = :userId OR p.recipient.id = :userId)")
  Page<Payment> findByContract(@Param("id") Long id, @Param("userId") Long userId, Pageable pageable);
}
