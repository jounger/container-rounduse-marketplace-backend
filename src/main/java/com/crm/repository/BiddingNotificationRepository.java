package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.BiddingNotification;

@Repository
public interface BiddingNotificationRepository extends JpaRepository<BiddingNotification, Long> {

  @Query(value = "FROM BiddingNotification bn WHERE bn.recipient.id = :id")
  Page<BiddingNotification> findByUser(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM BiddingNotification bn WHERE bn.recipient.username = :username")
  Page<BiddingNotification> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM BiddingNotification bn WHERE bn.recipient.id = :id AND bn.type = :status")
  Page<BiddingNotification> findByUserAndStatus(@Param("id") Long id,
      @Param("status") String status, Pageable pageable);

  @Query(value = "FROM BiddingNotification bn WHERE bn.recipient.username = :username AND bn.type = :status")
  Page<BiddingNotification> findByUserAndStatus(@Param("username") String username,
      @Param("status") String status, Pageable pageable);
}
