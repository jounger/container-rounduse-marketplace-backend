package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Quotation;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long>{

}
