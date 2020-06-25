package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.models.BillOfLading;

public interface BillOfLadingRepository extends JpaRepository<BillOfLading, Long> {
  Boolean existsByBillOfLadingNumber(String billOfLadingNumber);
}
