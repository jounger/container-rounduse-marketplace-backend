package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Inbound;

@Repository
public interface InboundRepository extends JpaRepository<Inbound, Long>{

}
