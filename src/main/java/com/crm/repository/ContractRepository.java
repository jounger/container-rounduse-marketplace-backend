package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>{

}
