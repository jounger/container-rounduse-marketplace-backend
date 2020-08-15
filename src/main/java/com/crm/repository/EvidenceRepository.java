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

  @Query(value = "SELECT e FROM Evidence e LEFT JOIN e.sender s "
      + "LEFT JOIN e.contract c LEFT JOIN c.combined cb LEFT JOIN cb.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE c.id = :id AND (bd.offeree.username = :username or b.bidder.username = :username)")
  Page<Evidence> findByContract(@Param("id") Long contractId, @Param("username") String username, Pageable pageable);

  @Query(value = "SELECT e FROM Evidence e LEFT JOIN e.contract c WHERE c.id = :id")
  Page<Evidence> findByContract(@Param("id") Long contractId, Pageable pageable);
  
  @Query(value = "SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END"
      + " FROM Evidence e LEFT JOIN e.contract c LEFT JOIN c.shippingInfos si"
      + " WHERE si.id = :id AND e.isValid = true")
  Boolean isEditableShippingInfo(@Param("id") Long shippingInfoId);
  
  @Query(value = "SELECT CASE WHEN COUNT(e) = 1 THEN TRUE ELSE FALSE END"
      + " FROM Evidence e LEFT JOIN e.contract c LEFT JOIN c.shippingInfos si"
      + " WHERE e.id = :id AND e.isValid = true")
  Boolean existsValidEvidence(@Param("id") Long evidenceId);
}
