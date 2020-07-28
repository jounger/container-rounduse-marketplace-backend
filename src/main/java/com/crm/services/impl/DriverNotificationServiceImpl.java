package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorConstant;
import com.crm.enums.EnumDriverNotification;
import com.crm.enums.EnumNotificationType;
import com.crm.exception.NotFoundException;
import com.crm.models.DriverNotification;
import com.crm.models.Outbound;
import com.crm.models.User;
import com.crm.payload.request.DriverNotificationRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.DriverNotificationRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.UserRepository;
import com.crm.services.DriverNotificationService;

@Service
public class DriverNotificationServiceImpl implements DriverNotificationService {

  @Autowired
  private UserRepository userRepositoty;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private DriverNotificationRepository driverNotificationRepository;

  @Override
  public DriverNotification createDriverNotification(DriverNotificationRequest request) {
    DriverNotification driverNotification = new DriverNotification();

    User recipient = userRepositoty.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.RECIPIENT_NOT_FOUND));
    driverNotification.setRecipient(recipient);

    driverNotification.setIsRead(false);
    driverNotification.setIsHide(false);
    driverNotification.setTitle(request.getTitle());

    Outbound relatedResource = outboundRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_RELATED_RESOURCE_NOT_FOUND));
    driverNotification.setRelatedResource(relatedResource);

    driverNotification.setMessage(request.getMessage());
    EnumDriverNotification action = EnumDriverNotification.findByName(request.getAction());
    driverNotification.setAction(action.name());
    EnumNotificationType type = EnumNotificationType.findByName(request.getType());
    driverNotification.setType(type.name());

    driverNotification.setSendDate(LocalDateTime.now());

    DriverNotification _driverNotification = driverNotificationRepository.save(driverNotification);
    return _driverNotification;
  }

  @Override
  public DriverNotification getDriverNotification(Long id) {
    DriverNotification driverNotification = driverNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND));
    return driverNotification;
  }

  @Override
  public Page<DriverNotification> getDriverNotifications(PaginationRequest request) {
    Page<DriverNotification> driverNotifications = driverNotificationRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return driverNotifications;
  }

  @Override
  public Page<DriverNotification> getDriverNotificationsByUser(Long recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<DriverNotification> driverNotifications = null;
    if (status != null && !status.isEmpty()) {
      driverNotifications = driverNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      driverNotifications = driverNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return driverNotifications;
  }

  @Override
  public Page<DriverNotification> getDriverNotificationsByUsername(String recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<DriverNotification> driverNotifications = null;
    if (status != null && !status.isEmpty()) {
      driverNotifications = driverNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      driverNotifications = driverNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return driverNotifications;
  }

  @Override
  public DriverNotification editDriverNotification(Long id, Map<String, Object> updates) {
    DriverNotification driverNotification = driverNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (updates.get("isRead") != null && isRead != null) {
      driverNotification.setIsRead(isRead);
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (updates.get("isHide") != null && isHide != null) {
      driverNotification.setIsHide(isHide);
    }

    DriverNotification _driverNotification = driverNotificationRepository.save(driverNotification);
    return _driverNotification;
  }

  @Override
  public void removeDriverNotification(Long id) {
    if (driverNotificationRepository.existsById(id)) {
      driverNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND);
    }
  }

}
