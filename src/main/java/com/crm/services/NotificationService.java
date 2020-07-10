package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Notification;
import com.crm.payload.request.PaginationRequest;

public interface NotificationService {
  Page<Notification> searchNotifications(PaginationRequest request, String search);
}
