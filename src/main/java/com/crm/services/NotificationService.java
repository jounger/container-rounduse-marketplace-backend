package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Notification;
import com.crm.payload.request.PaginationRequest;

public interface NotificationService {

  Page<Notification> searchNotifications(PaginationRequest request, String search);

  Page<Notification> getNotificationsByUser(String username, PaginationRequest request);

  Notification editNotification(Long id, Map<String, Object> updates);

  void removeNotification(Long id);
}
