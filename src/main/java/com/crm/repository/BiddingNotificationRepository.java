package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.BiddingNotification;

public interface BiddingNotificationRepository extends JpaRepository<BiddingNotification, Long>{
  
  @Query(value = "FROM BiddingNotification bn WHERE bn.recipient.id = :id")
  Page<BiddingNotification> findBiddingNotificationsByUser(@Param("id") Long id, Pageable pageable);

}
