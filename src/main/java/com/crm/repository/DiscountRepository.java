package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>{

  Optional<Discount> findByCode(String code);
  
  Boolean existsByCode(String code);
}
