package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Notification;

@Repository
public interface NotificationRepository
    extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

  @Query(value = "FROM Notification n WHERE n.recipient.id = :id")
  Page<Notification> findByUser(@Param("id") Long id, Pageable pageable);

  @Query(value = "FROM Notification n WHERE n.recipient.id = :id AND n.type = :status")
  Page<Notification> findByUserAndStatus(@Param("id") Long id, @Param("status") String status, Pageable pageable);
}
