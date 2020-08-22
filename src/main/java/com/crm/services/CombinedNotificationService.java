package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.CombinedNotification;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.CombinedNotificationRequest;

public interface CombinedNotificationService {

  CombinedNotification createShippingLineNotification(CombinedNotificationRequest request);

  CombinedNotification getShippingLineNotification(Long id);

  Page<CombinedNotification> getShippingLineNotifications(PaginationRequest request);

  Page<CombinedNotification> getShippingLineNotificationsByUser(Long recipient, PaginationRequest request);

  Page<CombinedNotification> getShippingLineNotificationsByUsername(String recipient, PaginationRequest request);

  CombinedNotification editShippingLineNotification(Long id, Map<String, Object> updates);

  void removeShippingLineNotification(Long id);
}
