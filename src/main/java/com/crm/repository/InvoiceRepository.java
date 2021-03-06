package com.crm.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

  @Query(value = "FROM Invoice p WHERE p.sender.username = :username OR p.recipient.username = :username")
  Page<Invoice> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM Invoice p WHERE p.contract.id = :id")
  Page<Invoice> findByContract(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM Invoice p WHERE p.contract.id = :id AND (p.sender.username = :username OR p.recipient.username = :username)")
  Page<Invoice> findByContract(@Param("id") Long id, @Param("username") String username, Pageable pageable);

  @Query(value = "SELECT CASE WHEN COUNT(i) = 0 THEN TRUE ELSE FALSE END FROM Invoice i WHERE i.sender.username = :username AND i.paymentDate <= :paymentTerm AND i.isPaid  = false")
  boolean checkInvoicePaymentDateAndIsPaid(@Param("username") String username,
      @Param("paymentTerm") LocalDateTime paymentTerm);

}
