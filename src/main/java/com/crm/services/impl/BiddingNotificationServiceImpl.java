package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.enums.EnumBiddingNotification;
import com.crm.enums.EnumNotificationType;
import com.crm.exception.NotFoundException;
import com.crm.models.BiddingDocument;
import com.crm.models.BiddingNotification;
import com.crm.models.User;
import com.crm.payload.request.BiddingNotificationRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.BiddingNotificationRepository;
import com.crm.repository.UserRepository;
import com.crm.services.BiddingNotificationService;

@Service
public class BiddingNotificationServiceImpl implements BiddingNotificationService {

  @Autowired
  private BiddingNotificationRepository biddingNotificationRepository;

  @Autowired
  private UserRepository userRepositoty;

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Override
  public BiddingNotification createBiddingNotification(BiddingNotificationRequest request) {
    BiddingNotification biddingNotification = new BiddingNotification();

    User recipient = userRepositoty.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RECIPIENT_NOT_FOUND));
    biddingNotification.setRecipient(recipient);

    biddingNotification.setIsRead(false);
    biddingNotification.setIsHide(false);
    biddingNotification.setTitle(request.getTitle());

    BiddingDocument relatedResource = biddingDocumentRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_RELATED_RESOURCE_NOT_FOUND));
    biddingNotification.setRelatedResource(relatedResource);

    biddingNotification.setMessage(request.getMessage());
    EnumBiddingNotification action = EnumBiddingNotification.findByName(request.getAction());
    biddingNotification.setAction(action.name());
    EnumNotificationType type = EnumNotificationType.findByName(request.getType());
    biddingNotification.setType(type.name());

    biddingNotification.setSendDate(LocalDateTime.now());

    BiddingNotification _biddingNotification = biddingNotificationRepository.save(biddingNotification);
    return _biddingNotification;
  }

  @Override
  public BiddingNotification getBiddingNotification(Long id) {
    BiddingNotification biddingNotification = biddingNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
    return biddingNotification;
  }

  @Override
  public Page<BiddingNotification> getBiddingNotifications(PaginationRequest request) {
    String status = request.getStatus();
    Page<BiddingNotification> biddingNotifications = null;
    if (status != null && !status.isEmpty()) {
      biddingNotifications = biddingNotificationRepository.findByType(status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      biddingNotifications = biddingNotificationRepository
          .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return biddingNotifications;
  }

  @Override
  public Page<BiddingNotification> getBiddingNotificationsByUser(Long id, PaginationRequest request) {
    String status = request.getStatus();
    Page<BiddingNotification> biddingNotifications = null;
    if (status != null && !status.isEmpty()) {
      biddingNotifications = biddingNotificationRepository.findByUserAndStatus(id, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      biddingNotifications = biddingNotificationRepository.findByUser(id,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return biddingNotifications;
  }

  @Override
  public Page<BiddingNotification> getBiddingNotificationsByUser(String recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<BiddingNotification> biddingNotifications = null;
    if (status != null && !status.isEmpty()) {
      biddingNotifications = biddingNotificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      biddingNotifications = biddingNotificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return biddingNotifications;
  }

  @Override
  public BiddingNotification editBiddingNotification(Long id, Map<String, Object> updates) {
    BiddingNotification biddingNotification = biddingNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (updates.get("isRead") != null && isRead != null) {
      biddingNotification.setIsRead(isRead);
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (updates.get("isHide") != null && isHide != null) {
      biddingNotification.setIsHide(isHide);
    }

    BiddingNotification _biddingNotification = biddingNotificationRepository.save(biddingNotification);
    return _biddingNotification;
  }

  @Override
  public void removeBiddingNotification(Long id) {
    if (biddingNotificationRepository.existsById(id)) {
      biddingNotificationRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND);
    }
  }
}
