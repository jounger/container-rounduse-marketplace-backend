package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.models.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long>{

}
