package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.BillOfLading;

@Repository
public interface BillOfLadingRepository extends JpaRepository<BillOfLading, Long> {
  Boolean existsByBillOfLadingNumber(String billOfLadingNumber);
}
