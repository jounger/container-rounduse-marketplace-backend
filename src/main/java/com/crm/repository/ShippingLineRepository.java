package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.ShippingLine;

@Repository
public interface ShippingLineRepository extends JpaRepository<ShippingLine, Long>{

}
