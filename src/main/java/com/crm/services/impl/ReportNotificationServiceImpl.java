package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumReportNotificationType;
import com.crm.exception.NotFoundException;
import com.crm.models.Report;
import com.crm.models.ReportNotification;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportNotificationRequest;
import com.crm.repository.ReportNotificationRepository;
import com.crm.repository.ReportRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ReportNotificationService;

@Service
public class ReportNotificationServiceImpl implements ReportNotificationService {

  @Autowired
  private UserRepository userRepositoty;

  @Autowired
  private ReportRepository reportRepository;

  @Autowired
  private ReportNotificationRepository reportNotificationRepository;

  @Override
  public ReportNotification createReportNotification(ReportNotificationRequest request) {
    ReportNotification reportNotification = new ReportNotification();

    User recipient = userRepositoty.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException("Recipient is not found."));
    reportNotification.setRecipient(recipient);

    reportNotification.setIsRead(false);

    Report relatedResource = reportRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException("Related resource is not found."));
    reportNotification.setRelatedResource(relatedResource);

    reportNotification.setMessage(request.getMessage());
    EnumReportNotificationType type = EnumReportNotificationType.findByName(request.getType());
    reportNotification.setType(type.name());

    reportNotification.setSendDate(LocalDateTime.now());

    reportNotificationRepository.save(reportNotification);
    return reportNotification;
  }

  @Override
  public ReportNotification getReportNotification(Long id) {
    ReportNotification reportNotification = reportNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Report Notification is not found."));
    return reportNotification;
  }

  @Override
  public Page<ReportNotification> getReportNotifications(PaginationRequest request) {
    Page<ReportNotification> reportNotifications = reportNotificationRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return reportNotifications;
  }

  @Override
  public Page<ReportNotification> getReportNotificationsByUser(Long recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<ReportNotification> reportNotifications = null;
    if (status != null && !status.isEmpty()) {
      reportNotifications = reportNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      reportNotifications = reportNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return reportNotifications;
  }

  @Override
  public Page<ReportNotification> getReportNotificationsByUsername(String recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<ReportNotification> reportNotifications = null;
    if (status != null && !status.isEmpty()) {
      reportNotifications = reportNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      reportNotifications = reportNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return reportNotifications;
  }

  @Override
  public ReportNotification editReportNotification(Long id, Map<String, Object> updates) {
    ReportNotification reportNotification = reportNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Report Notification is not found."));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (isRead != null) {
      reportNotification.setIsRead(isRead);
    } else {
      throw new NotFoundException("Is Read is not found.");
    }

    reportNotificationRepository.save(reportNotification);
    return reportNotification;
  }

  @Override
  public void removeReportNotification(Long id) {
    if (reportNotificationRepository.existsById(id)) {
      reportNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException("Report Notification is not found.");
    }
  }

}
