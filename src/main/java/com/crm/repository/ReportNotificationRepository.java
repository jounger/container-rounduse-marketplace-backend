package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.ReportNotification;

@Repository
public interface ReportNotificationRepository extends JpaRepository<ReportNotification, Long> {

  @Query(value = "FROM ReportNotification rn WHERE rn.recipient.id = :id")
  Page<ReportNotification> findByUser(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM ReportNotification rn WHERE rn.recipient.username = :username")
  Page<ReportNotification> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM ReportNotification rn WHERE rn.recipient.id = :id AND rn.action = :status")
  Page<ReportNotification> findByUserAndStatus(@Param("id") Long id, @Param("status") String status, Pageable pageable);

  @Query(value = "FROM ReportNotification rn WHERE rn.recipient.username = :username AND rn.action = :status")
  Page<ReportNotification> findByUserAndStatus(@Param("username") String username, @Param("status") String status,
      Pageable pageable);

  @Query(value = "FROM ReportNotification rn WHERE rn.type = :status")
  Page<ReportNotification> findByType(@Param("status") String status, Pageable pageable);
}
