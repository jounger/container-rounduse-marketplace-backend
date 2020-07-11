package com.crm.repository;

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
      + "WHERE bd.offeree.username = :username or b.bidder.username = :username")
  Page<Contract> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "SELECT c FROM Contract c LEFT JOIN c.combined cb "
      + "LEFT JOIN cb.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE cb.id = :id AND(bd.offeree.username = :username or b.bidder.username = :username)")
  Optional<Contract> findByCombined(@Param("id") Long combined, String username);
}
