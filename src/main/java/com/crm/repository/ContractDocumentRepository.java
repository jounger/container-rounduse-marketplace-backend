package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.ContractDocument;

@Repository
public interface ContractDocumentRepository extends JpaRepository<ContractDocument, Long>, JpaSpecificationExecutor<ContractDocument> {

  @Query(value = "SELECT e FROM ContractDocument e LEFT JOIN e.sender s "
      + "LEFT JOIN s.bids b LEFT JOIN s.biddingDocuments bd "
      + "WHERE bd.offeree.username = :username or b.bidder.username = :username")
  Page<ContractDocument> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "SELECT e FROM ContractDocument e LEFT JOIN e.sender s "
      + "LEFT JOIN e.contract c LEFT JOIN c.combined cb LEFT JOIN cb.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE c.id = :id AND (bd.offeree.username = :username or b.bidder.username = :username)")
  Page<ContractDocument> findByContract(@Param("id") Long contractId, @Param("username") String username, Pageable pageable);

  @Query(value = "SELECT e FROM ContractDocument e LEFT JOIN e.contract c WHERE c.id = :id")
  Page<ContractDocument> findByContract(@Param("id") Long contractId, Pageable pageable);

  @Query(value = "SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END"
      + " FROM ContractDocument e LEFT JOIN e.contract c LEFT JOIN c.shippingInfos si"
      + " WHERE si.id = :id AND e.status = :status")
  Boolean isEditableShippingInfo(@Param("id") Long shippingInfoId, @Param("status") String status);

  @Query(value = "SELECT CASE WHEN COUNT(e) = 1 THEN TRUE ELSE FALSE END"
      + " FROM ContractDocument e LEFT JOIN e.contract c LEFT JOIN c.shippingInfos si"
      + " WHERE e.id = :id AND e.status = :status")
  Boolean existsValidContractDocument(@Param("id") Long evidenceId, @Param("status") String status);
}
