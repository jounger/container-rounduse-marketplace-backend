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

  @Query(value = "FROM Notification n WHERE n.recipient.username = :username")
  Page<Notification> findByUser(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM Notification n WHERE n.recipient.username = :username AND n.type = :status")
  Page<Notification> findByUserAndStatus(@Param("username") String username, @Param("status") String status, Pageable pageable);
}
