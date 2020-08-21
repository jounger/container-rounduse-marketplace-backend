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
import com.crm.enums.EnumCombinedNotification;
import com.crm.exception.NotFoundException;
import com.crm.models.Combined;
import com.crm.models.CombinedNotification;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.CombinedNotificationRequest;
import com.crm.repository.CombinedRepository;
import com.crm.repository.CombinedNotificationRepository;
import com.crm.repository.UserRepository;
import com.crm.services.CombinedNotificationService;

@Service
public class CombinedNotificationServiceImpl implements CombinedNotificationService {

  @Autowired
  private UserRepository userRepositoty;

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private CombinedNotificationRepository shippingLineNotificationRepository;

  @Override
  public CombinedNotification createShippingLineNotification(CombinedNotificationRequest request) {
    CombinedNotification shippingLineNotification = new CombinedNotification();

    User recipient = userRepositoty.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RECIPIENT_NOT_FOUND));
    shippingLineNotification.setRecipient(recipient);

    shippingLineNotification.setIsRead(false);
    shippingLineNotification.setIsHide(false);
    shippingLineNotification.setTitle(request.getTitle());

    Combined relatedResource = combinedRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_RELATED_RESOURCE_NOT_FOUND));
    shippingLineNotification.setRelatedResource(relatedResource);

    shippingLineNotification.setMessage(request.getMessage());
    EnumCombinedNotification action = EnumCombinedNotification.findByName(request.getAction());
    shippingLineNotification.setAction(action.name());
    EnumNotificationType type = EnumNotificationType.findByName(request.getType());
    shippingLineNotification.setType(type.name());

    shippingLineNotification.setSendDate(LocalDateTime.now());

    CombinedNotification _shippingLineNotification = shippingLineNotificationRepository
        .save(shippingLineNotification);
    return _shippingLineNotification;
  }

  @Override
  public CombinedNotification getShippingLineNotification(Long id) {
    CombinedNotification shippingLineNotification = shippingLineNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
    return shippingLineNotification;
  }

  @Override
  public Page<CombinedNotification> getShippingLineNotifications(PaginationRequest request) {
    String status = request.getStatus();
    Page<CombinedNotification> shippingLineNotification = null;
    if (status != null && !status.isEmpty()) {
      shippingLineNotification = shippingLineNotificationRepository.findByType(status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      shippingLineNotification = shippingLineNotificationRepository
          .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return shippingLineNotification;
  }

  @Override
  public Page<CombinedNotification> getShippingLineNotificationsByUser(Long recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<CombinedNotification> shippingLineNotification = null;
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
  public Page<CombinedNotification> getShippingLineNotificationsByUsername(String recipient,
      PaginationRequest request) {
    String status = request.getStatus();
    Page<CombinedNotification> shippingLineNotifications = null;
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
  public CombinedNotification editShippingLineNotification(Long id, Map<String, Object> updates) {
    CombinedNotification shippingLineNotification = shippingLineNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (updates.get("isRead") != null && isRead != null) {
      shippingLineNotification.setIsRead(isRead);
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (updates.get("isHide") != null && isHide != null) {
      shippingLineNotification.setIsHide(isHide);
    }

    CombinedNotification _shippingLineNotification = shippingLineNotificationRepository
        .save(shippingLineNotification);
    return _shippingLineNotification;
  }

  @Override
  public void removeShippingLineNotification(Long id) {
    if (shippingLineNotificationRepository.existsById(id)) {
      shippingLineNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND);
    }
  }

}
