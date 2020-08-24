package com.crm.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

  @Query(value = "SELECT c FROM Contract c LEFT JOIN c.combined cb "
      + "LEFT JOIN cb.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE bd.offeree.username = :username OR b.bidder.username = :username")
  Page<Contract> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "SELECT c FROM Contract c LEFT JOIN c.combined cb "
      + "LEFT JOIN cb.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE cb.id = :id AND(bd.offeree.username = :username OR b.bidder.username = :username)")
  Optional<Contract> findByCombined(@Param("id") Long combined, String username);

  @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Contract c LEFT JOIN c.combined cb "
      + "LEFT JOIN cb.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE c.id = :id AND (bd.offeree.username = :username OR b.bidder.username = :username)")
  Boolean existsByUserAndContract(@Param("id") Long contractId, @Param("username") String username);

  @Query(value = "SELECT COUNT(DISTINCT c) FROM Contract c"
      + " WHERE c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContractsByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(DISTINCT c) FROM Contract c LEFT JOIN c.invoices i"
      + " WHERE c.createdAt > :startDate AND c.createdAt < :endDate AND i != NULL AND i.isPaid = 1")
  Integer countPaidContractsByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(DISTINCT c) FROM Contract c LEFT JOIN c.invoices i"
      + " WHERE c.createdAt > :startDate AND c.createdAt < :endDate"
      + " AND (i IS NULL OR (i != NULL AND i.isPaid = 0))")
  Integer countUnpaidContractsByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(DISTINCT c) FROM Contract c WHERE (c.sender.username = :username"
      + " OR c.combined.bid.bidder.username = :username) AND c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContracts(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(DISTINCT c) FROM Contract c LEFT JOIN c.invoices i"
      + " WHERE (c.sender.username = :username OR c.combined.bid.bidder.username = :username)"
      + " AND c.createdAt > :startDate AND c.createdAt < :endDate AND (i != NULL AND i.isPaid = 1)")
  Integer countPaidContracts(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(DISTINCT c) FROM Contract c LEFT JOIN c.invoices i"
      + " WHERE (c.sender.username = :username OR c.combined.bid.bidder.username = :username)"
      + " AND c.createdAt > :startDate AND c.createdAt < :endDate" + " AND (i IS NULL OR (i != NULL AND i.isPaid = 0))")
  Integer countUnpaidContracts(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END FROM Contract c"
      + " LEFT JOIN c.invoices i WHERE c.id = :id AND (i IS NULL OR (i != NULL AND i.isPaid = 0))")
  Boolean isUnpaidContract(@Param("id") Long id);
}
