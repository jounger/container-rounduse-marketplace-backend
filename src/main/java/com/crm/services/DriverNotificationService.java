package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.DriverNotification;
import com.crm.payload.request.DriverNotificationRequest;
import com.crm.payload.request.PaginationRequest;

public interface DriverNotificationService {

  DriverNotification createDriverNotification(DriverNotificationRequest request);

  DriverNotification getDriverNotification(Long id);

  Page<DriverNotification> getDriverNotifications(PaginationRequest request);

  Page<DriverNotification> getDriverNotificationsByUser(Long recipient, PaginationRequest request);

  Page<DriverNotification> getDriverNotificationsByUsername(String recipient, PaginationRequest request);

  DriverNotification editDriverNotification(Long id, Map<String, Object> updates);

  void removeDriverNotification(Long id);
}
