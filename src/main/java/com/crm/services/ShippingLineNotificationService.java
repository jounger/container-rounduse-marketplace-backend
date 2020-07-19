package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ShippingLineNotification;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingLineNotificationRequest;

public interface ShippingLineNotificationService {

  ShippingLineNotification createShippingLineNotification(ShippingLineNotificationRequest request);

  ShippingLineNotification getShippingLineNotification(Long id);

  Page<ShippingLineNotification> getShippingLineNotifications(PaginationRequest request);

  Page<ShippingLineNotification> getShippingLineNotificationsByUser(Long recipient, PaginationRequest request);

  Page<ShippingLineNotification> getShippingLineNotificationsByUsername(String recipient, PaginationRequest request);

  ShippingLineNotification editShippingLineNotification(Long id, Map<String, Object> updates);

  void removeShippingLineNotification(Long id);
}
