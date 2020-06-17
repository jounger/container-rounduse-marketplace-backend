package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.BiddingDocument;

@Repository
public interface BiddingDoccumentRepository extends JpaRepository<BiddingDocument, Long>{

}
