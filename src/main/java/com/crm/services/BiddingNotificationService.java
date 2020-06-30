package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.BiddingNotification;
import com.crm.payload.request.BiddingNotificationRequest;
import com.crm.payload.request.PaginationRequest;

public interface BiddingNotificationService {
  
  BiddingNotification createBiddingNotification(BiddingNotificationRequest request);
  
  BiddingNotification getBiddingNotification(Long id);
  
  Page<BiddingNotification> getBiddingNotifications(PaginationRequest request);
  
  Page<BiddingNotification> getBiddingNotificationsByUser(Long id, PaginationRequest request);
  
  BiddingNotification editBiddingNotification(Long id, Map<String, Object> updates);
  
  void removeBiddingNotification(Long id);
}
