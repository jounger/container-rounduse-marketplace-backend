package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.ShippingLineNotification;

public interface ShippingLineNotificationRepository extends JpaRepository<ShippingLineNotification, Long> {

  @Query(value = "FROM ShippingLineNotification sn WHERE sn.recipient.id = :id")
  Page<ShippingLineNotification> findByUser(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM ShippingLineNotification sn WHERE sn.recipient.username = :username")
  Page<ShippingLineNotification> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM ShippingLineNotification sn WHERE sn.recipient.id = :id AND sn.type = :status")
  Page<ShippingLineNotification> findByUserAndStatus(@Param("id") Long id, @Param("status") String status,
      Pageable pageable);

  @Query(value = "FROM ShippingLineNotification sn WHERE sn.recipient.username = :username AND sn.type = :status")
  Page<ShippingLineNotification> findByUserAndStatus(@Param("username") String username, @Param("status") String status,
      Pageable pageable);
}
