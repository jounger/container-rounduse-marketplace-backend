package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Consignment;

@Repository
public interface ConsignmentRepository extends JpaRepository<Consignment, Long>{

}
