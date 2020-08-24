package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.CombinedNotification;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.CombinedNotificationRequest;

public interface CombinedNotificationService {

  CombinedNotification createCombinedNotification(CombinedNotificationRequest request);

  CombinedNotification getCombinedNotification(Long id);

  Page<CombinedNotification> getCombinedNotifications(PaginationRequest request);

  Page<CombinedNotification> getCombinedNotificationsByUser(Long recipient, PaginationRequest request);

  Page<CombinedNotification> getCombinedNotificationsByUsername(String recipient, PaginationRequest request);

  CombinedNotification editCombinedNotification(Long id, Map<String, Object> updates);

  void removeCombinedNotification(Long id);
}
