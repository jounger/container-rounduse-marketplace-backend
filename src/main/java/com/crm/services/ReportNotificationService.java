package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ReportNotification;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportNotificationRequest;

public interface ReportNotificationService {

  ReportNotification createReportNotification(ReportNotificationRequest request);

  ReportNotification getReportNotification(Long id);

  Page<ReportNotification> getReportNotifications(PaginationRequest request);

  Page<ReportNotification> getReportNotificationsByUser(Long recipient, PaginationRequest request);

  Page<ReportNotification> getReportNotificationsByUsername(String recipient, PaginationRequest request);

  ReportNotification editReportNotification(Long id, Map<String, Object> updates);

  void removeReportNotification(Long id);
}
