package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Supply;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {

  Boolean existsByCode(String code);

}
