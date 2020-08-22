package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ShippingNotification;
import com.crm.payload.request.ShippingNotificationRequest;
import com.crm.payload.request.PaginationRequest;

public interface ShippingNotificationService {

  ShippingNotification createDriverNotification(ShippingNotificationRequest request);

  ShippingNotification getDriverNotification(Long id);

  Page<ShippingNotification> getDriverNotifications(PaginationRequest request);

  Page<ShippingNotification> getDriverNotificationsByUser(Long recipient, PaginationRequest request);

  Page<ShippingNotification> getDriverNotificationsByUsername(String recipient, PaginationRequest request);

  ShippingNotification editDriverNotification(Long id, Map<String, Object> updates);

  void removeDriverNotification(Long id);
}
