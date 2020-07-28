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
import com.crm.enums.EnumShippingLineNotification;
import com.crm.exception.NotFoundException;
import com.crm.models.Combined;
import com.crm.models.ShippingLineNotification;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingLineNotificationRequest;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ShippingLineNotificationRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ShippingLineNotificationService;

@Service
public class ShippingLineNotificationServiceImpl implements ShippingLineNotificationService {

  @Autowired
  private UserRepository userRepositoty;

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private ShippingLineNotificationRepository shippingLineNotificationRepository;

  @Override
  public ShippingLineNotification createShippingLineNotification(ShippingLineNotificationRequest request) {
    ShippingLineNotification shippingLineNotification = new ShippingLineNotification();

    User recipient = userRepositoty.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.RECIPIENT_NOT_FOUND));
    shippingLineNotification.setRecipient(recipient);

    shippingLineNotification.setIsRead(false);
    shippingLineNotification.setIsHide(false);
    shippingLineNotification.setTitle(request.getTitle());

    Combined relatedResource = combinedRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_RELATED_RESOURCE_NOT_FOUND));
    shippingLineNotification.setRelatedResource(relatedResource);

    shippingLineNotification.setMessage(request.getMessage());
    EnumShippingLineNotification action = EnumShippingLineNotification.findByName(request.getAction());
    shippingLineNotification.setAction(action.name());
    EnumNotificationType type = EnumNotificationType.findByName(request.getType());
    shippingLineNotification.setType(type.name());

    shippingLineNotification.setSendDate(LocalDateTime.now());

    ShippingLineNotification _shippingLineNotification = shippingLineNotificationRepository
        .save(shippingLineNotification);
    return _shippingLineNotification;
  }

  @Override
  public ShippingLineNotification getShippingLineNotification(Long id) {
    ShippingLineNotification shippingLineNotification = shippingLineNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND));
    return shippingLineNotification;
  }

  @Override
  public Page<ShippingLineNotification> getShippingLineNotifications(PaginationRequest request) {
    Page<ShippingLineNotification> shippingLineNotification = shippingLineNotificationRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return shippingLineNotification;
  }

  @Override
  public Page<ShippingLineNotification> getShippingLineNotificationsByUser(Long recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<ShippingLineNotification> shippingLineNotification = null;
    if (status != null && !status.isEmpty()) {
      shippingLineNotification = shippingLineNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      shippingLineNotification = shippingLineNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return shippingLineNotification;
  }

  @Override
  public Page<ShippingLineNotification> getShippingLineNotificationsByUsername(String recipient,
      PaginationRequest request) {
    String status = request.getStatus();
    Page<ShippingLineNotification> shippingLineNotifications = null;
    if (status != null && !status.isEmpty()) {
      shippingLineNotifications = shippingLineNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      shippingLineNotifications = shippingLineNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return shippingLineNotifications;
  }

  @Override
  public ShippingLineNotification editShippingLineNotification(Long id, Map<String, Object> updates) {
    ShippingLineNotification shippingLineNotification = shippingLineNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (updates.get("isRead") != null && isRead != null) {
      shippingLineNotification.setIsRead(isRead);
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (updates.get("isHide") != null && isHide != null) {
      shippingLineNotification.setIsHide(isHide);
    }

    ShippingLineNotification _shippingLineNotification = shippingLineNotificationRepository
        .save(shippingLineNotification);
    return _shippingLineNotification;
  }

  @Override
  public void removeShippingLineNotification(Long id) {
    if (shippingLineNotificationRepository.existsById(id)) {
      shippingLineNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.NOTIFICATION_NOT_FOUND);
    }
  }

}
