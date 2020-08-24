package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.ShippingNotification;

@Repository
public interface ShippingNotificationRepository extends JpaRepository<ShippingNotification, Long> {

  @Query(value = "FROM ShippingNotification dn WHERE dn.recipient.id = :id")
  Page<ShippingNotification> findByUser(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM ShippingNotification dn WHERE dn.recipient.username = :username")
  Page<ShippingNotification> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM ShippingNotification dn WHERE dn.recipient.id = :id AND dn.action = :status")
  Page<ShippingNotification> findByUserAndStatus(@Param("id") Long id, @Param("status") String status,
      Pageable pageable);

  @Query(value = "FROM ShippingNotification dn WHERE dn.recipient.username = :username AND dn.action = :status")
  Page<ShippingNotification> findByUserAndStatus(@Param("username") String username, @Param("status") String status,
      Pageable pageable);

  @Query(value = "FROM ShippingNotification dn WHERE dn.type = :status")
  Page<ShippingNotification> findByType(@Param("status") String status, Pageable pageable);
}
