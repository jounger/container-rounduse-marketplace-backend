package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumDriverNotificationType;
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
        .orElseThrow(() -> new NotFoundException("Recipient is not found."));
    driverNotification.setRecipient(recipient);

    driverNotification.setIsRead(false);
    driverNotification.setIsHide(false);
    driverNotification.setTitle(request.getTitle());

    Outbound relatedResource = outboundRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException("Related resource is not found."));
    driverNotification.setRelatedResource(relatedResource);

    driverNotification.setMessage(request.getMessage());
    EnumDriverNotificationType type = EnumDriverNotificationType.findByName(request.getType());
    driverNotification.setType(type.name());

    driverNotification.setSendDate(LocalDateTime.now());

    driverNotificationRepository.save(driverNotification);
    return driverNotification;
  }

  @Override
  public DriverNotification getDriverNotification(Long id) {
    DriverNotification driverNotification = driverNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Driver Notification is not found."));
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
        .orElseThrow(() -> new NotFoundException("Driver Notification is not found."));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (isRead != null) {
      driverNotification.setIsRead(isRead);
    } else {
      throw new NotFoundException("Is Read is not found.");
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (isHide != null) {
      driverNotification.setIsHide(isHide);
    } else {
      throw new NotFoundException("Is Hide is not found.");
    }

    driverNotificationRepository.save(driverNotification);
    return driverNotification;
  }

  @Override
  public void removeDriverNotification(Long id) {
    if (driverNotificationRepository.existsById(id)) {
      driverNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException("Driver Notification is not found.");
    }
  }

}
