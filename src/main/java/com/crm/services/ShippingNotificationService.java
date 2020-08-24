package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ShippingNotification;
import com.crm.payload.request.ShippingNotificationRequest;
import com.crm.payload.request.PaginationRequest;

public interface ShippingNotificationService {

  ShippingNotification createShippingNotification(ShippingNotificationRequest request);

  ShippingNotification getShippingNotification(Long id);

  Page<ShippingNotification> getShippingNotifications(PaginationRequest request);

  Page<ShippingNotification> getShippingNotificationsByUser(Long recipient, PaginationRequest request);

  Page<ShippingNotification> getShippingNotificationsByUsername(String recipient, PaginationRequest request);

  ShippingNotification editShippingNotification(Long id, Map<String, Object> updates);

  void removeShippingNotification(Long id);
}
