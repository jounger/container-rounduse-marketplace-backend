package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.enums.EnumCombinedNotification;
import com.crm.enums.EnumNotificationType;
import com.crm.exception.NotFoundException;
import com.crm.models.Combined;
import com.crm.models.CombinedNotification;
import com.crm.models.User;
import com.crm.payload.request.CombinedNotificationRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.CombinedNotificationRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.UserRepository;
import com.crm.services.CombinedNotificationService;

@Service
public class CombinedNotificationServiceImpl implements CombinedNotificationService {

  @Autowired
  private UserRepository userRepositoty;

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private CombinedNotificationRepository combinedNotificationRepository;

  @Override
  public CombinedNotification createCombinedNotification(CombinedNotificationRequest request) {
    CombinedNotification combinedNotification = new CombinedNotification();

    User recipient = userRepositoty.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RECIPIENT_NOT_FOUND));
    combinedNotification.setRecipient(recipient);

    combinedNotification.setIsRead(false);
    combinedNotification.setIsHide(false);
    combinedNotification.setTitle(request.getTitle());

    Combined relatedResource = combinedRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_RELATED_RESOURCE_NOT_FOUND));
    combinedNotification.setRelatedResource(relatedResource);

    combinedNotification.setMessage(request.getMessage());
    EnumCombinedNotification action = EnumCombinedNotification.findByName(request.getAction());
    combinedNotification.setAction(action.name());
    EnumNotificationType type = EnumNotificationType.findByName(request.getType());
    combinedNotification.setType(type.name());

    combinedNotification.setSendDate(LocalDateTime.now());

    CombinedNotification _combinedNotification = combinedNotificationRepository.save(combinedNotification);
    return _combinedNotification;
  }

  @Override
  public CombinedNotification getCombinedNotification(Long id) {
    CombinedNotification combinedNotification = combinedNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
    return combinedNotification;
  }

  @Override
  public Page<CombinedNotification> getCombinedNotifications(PaginationRequest request) {
    String status = request.getStatus();
    Page<CombinedNotification> combinedNotification = null;
    if (status != null && !status.isEmpty()) {
      combinedNotification = combinedNotificationRepository.findByType(status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      combinedNotification = combinedNotificationRepository
          .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return combinedNotification;
  }

  @Override
  public Page<CombinedNotification> getCombinedNotificationsByUser(Long recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<CombinedNotification> combinedNotification = null;
    if (status != null && !status.isEmpty()) {
      combinedNotification = combinedNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      combinedNotification = combinedNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return combinedNotification;
  }

  @Override
  public Page<CombinedNotification> getCombinedNotificationsByUsername(String recipient,
      PaginationRequest request) {
    String status = request.getStatus();
    Page<CombinedNotification> combinedNotifications = null;
    if (status != null && !status.isEmpty()) {
      combinedNotifications = combinedNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      combinedNotifications = combinedNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return combinedNotifications;
  }

  @Override
  public CombinedNotification editCombinedNotification(Long id, Map<String, Object> updates) {
    CombinedNotification combinedNotification = combinedNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (updates.get("isRead") != null && isRead != null) {
      combinedNotification.setIsRead(isRead);
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (updates.get("isHide") != null && isHide != null) {
      combinedNotification.setIsHide(isHide);
    }

    CombinedNotification _combinedNotification = combinedNotificationRepository.save(combinedNotification);
    return _combinedNotification;
  }

  @Override
  public void removeCombinedNotification(Long id) {
    if (combinedNotificationRepository.existsById(id)) {
      combinedNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND);
    }
  }

}
