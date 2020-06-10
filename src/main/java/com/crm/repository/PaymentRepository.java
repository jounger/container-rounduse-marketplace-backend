package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{

}
