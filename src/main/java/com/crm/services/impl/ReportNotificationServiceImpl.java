package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorConstant;
import com.crm.enums.EnumNotificationType;
import com.crm.enums.EnumReportNotification;
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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.RECIPIENT_NOT_FOUND));
    reportNotification.setRecipient(recipient);

    reportNotification.setIsRead(false);
    reportNotification.setIsHide(false);
    reportNotification.setTitle(request.getTitle());

    Report relatedResource = reportRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_RELATED_RESOURCE_NOT_FOUND));
    reportNotification.setRelatedResource(relatedResource);

    reportNotification.setMessage(request.getMessage());
    EnumReportNotification action = EnumReportNotification.findByName(request.getAction());
    reportNotification.setAction(action.name());
    EnumNotificationType type = EnumNotificationType.findByName(request.getType());
    reportNotification.setType(type.name());

    reportNotification.setSendDate(LocalDateTime.now());

    ReportNotification _reportNotification = reportNotificationRepository.save(reportNotification);
    return _reportNotification;
  }

  @Override
  public ReportNotification getReportNotification(Long id) {
    ReportNotification reportNotification = reportNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND));
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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (updates.get("isRead") != null && isRead != null) {
      reportNotification.setIsRead(isRead);
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (updates.get("isHide") != null && isHide != null) {
      reportNotification.setIsHide(isHide);
    }

    ReportNotification _reportNotification = reportNotificationRepository.save(reportNotification);
    return _reportNotification;
  }

  @Override
  public void removeReportNotification(Long id) {
    if (reportNotificationRepository.existsById(id)) {
      reportNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND);
    }
  }

}
