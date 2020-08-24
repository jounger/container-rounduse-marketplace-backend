package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.enums.EnumNotificationType;
import com.crm.enums.EnumShippingNotification;
import com.crm.exception.NotFoundException;
import com.crm.models.ShippingInfo;
import com.crm.models.ShippingNotification;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingNotificationRequest;
import com.crm.repository.ShippingInfoRepository;
import com.crm.repository.ShippingNotificationRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ShippingNotificationService;

@Service
public class ShippingNotificationServiceImpl implements ShippingNotificationService {

  @Autowired
  private UserRepository userRepositoty;

  @Autowired
  private ShippingInfoRepository shippingInfoRepository;

  @Autowired
  private ShippingNotificationRepository shippingNotificationRepository;

  @Override
  public ShippingNotification createShippingNotification(ShippingNotificationRequest request) {
    ShippingNotification driverNotification = new ShippingNotification();

    User recipient = userRepositoty.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RECIPIENT_NOT_FOUND));
    driverNotification.setRecipient(recipient);

    driverNotification.setIsRead(false);
    driverNotification.setIsHide(false);
    driverNotification.setTitle(request.getTitle());

    ShippingInfo relatedResource = shippingInfoRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_RELATED_RESOURCE_NOT_FOUND));
    driverNotification.setRelatedResource(relatedResource);

    driverNotification.setMessage(request.getMessage());
    EnumShippingNotification action = EnumShippingNotification.findByName(request.getAction());
    driverNotification.setAction(action.name());
    EnumNotificationType type = EnumNotificationType.findByName(request.getType());
    driverNotification.setType(type.name());

    driverNotification.setSendDate(LocalDateTime.now());

    ShippingNotification _driverNotification = shippingNotificationRepository.save(driverNotification);
    return _driverNotification;
  }

  @Override
  public ShippingNotification getShippingNotification(Long id) {
    ShippingNotification driverNotification = shippingNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
    return driverNotification;
  }

  @Override
  public Page<ShippingNotification> getShippingNotifications(PaginationRequest request) {
    String status = request.getStatus();
    Page<ShippingNotification> driverNotifications = null;
    if (status != null && !status.isEmpty()) {
      driverNotifications = shippingNotificationRepository.findByType(status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      driverNotifications = shippingNotificationRepository
          .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return driverNotifications;
  }

  @Override
  public Page<ShippingNotification> getShippingNotificationsByUser(Long recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<ShippingNotification> driverNotifications = null;
    if (status != null && !status.isEmpty()) {
      driverNotifications = shippingNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      driverNotifications = shippingNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return driverNotifications;
  }

  @Override
  public Page<ShippingNotification> getShippingNotificationsByUsername(String recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<ShippingNotification> driverNotifications = null;
    if (status != null && !status.isEmpty()) {
      driverNotifications = shippingNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      driverNotifications = shippingNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return driverNotifications;
  }

  @Override
  public ShippingNotification editShippingNotification(Long id, Map<String, Object> updates) {
    ShippingNotification driverNotification = shippingNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (updates.get("isRead") != null && isRead != null) {
      driverNotification.setIsRead(isRead);
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (updates.get("isHide") != null && isHide != null) {
      driverNotification.setIsHide(isHide);
    }

    ShippingNotification _driverNotification = shippingNotificationRepository.save(driverNotification);
    return _driverNotification;
  }

  @Override
  public void removeShippingNotification(Long id) {
    if (shippingNotificationRepository.existsById(id)) {
      shippingNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND);
    }
  }

}
