package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.DriverNotification;

@Repository
public interface DriverNotificationRepository extends JpaRepository<DriverNotification, Long> {

  @Query(value = "FROM DriverNotification dn WHERE dn.recipient.id = :id")
  Page<DriverNotification> findDriverNotificationsByUser(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM DriverNotification dn WHERE dn.recipient.username = :username")
  Page<DriverNotification> findDriverNotificationsByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM DriverNotification dn WHERE dn.recipient.id = :id AND dn.type = :status")
  Page<DriverNotification> findDriverNotificationsByUserAndStatus(@Param("id") Long id, @Param("status") String status,
      Pageable pageable);

  @Query(value = "FROM DriverNotification dn WHERE dn.recipient.username = :username AND dn.type = :status")
  Page<DriverNotification> findDriverNotificationsByUserAndStatus(@Param("username") String username,
      @Param("status") String status, Pageable pageable);
}
