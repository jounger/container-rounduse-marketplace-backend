package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Evidence;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long>, JpaSpecificationExecutor<Evidence> {

  @Query(value = "SELECT e FROM Evidence e LEFT JOIN e.sender s "
      + "LEFT JOIN s.bids b LEFT JOIN s.biddingDocuments bd "
      + "WHERE bd.offeree.username = :username or b.bidder.username = :username")
  Page<Evidence> findByUser(@Param("username") String username, Pageable pageable);
}
