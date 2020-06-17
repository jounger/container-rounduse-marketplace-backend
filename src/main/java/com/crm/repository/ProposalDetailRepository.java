package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Bid;

@Repository
public interface ProposalDetailRepository extends JpaRepository<Bid, Long>{

}
