package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.CombinedNotification;

public interface CombinedNotificationRepository extends JpaRepository<CombinedNotification, Long> {

  @Query(value = "FROM CombinedNotification sn WHERE sn.recipient.id = :id")
  Page<CombinedNotification> findByUser(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM CombinedNotification sn WHERE sn.recipient.username = :username")
  Page<CombinedNotification> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM CombinedNotification sn WHERE sn.recipient.id = :id AND sn.action = :status")
  Page<CombinedNotification> findByUserAndStatus(@Param("id") Long id, @Param("status") String status,
      Pageable pageable);

  @Query(value = "FROM CombinedNotification sn WHERE sn.recipient.username = :username AND sn.action = :status")
  Page<CombinedNotification> findByUserAndStatus(@Param("username") String username, @Param("status") String status,
      Pageable pageable);

  @Query(value = "FROM CombinedNotification sn WHERE sn.type = :status")
  Page<CombinedNotification> findByType(@Param("status") String status, Pageable pageable);
}
