package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumBiddingNotificationType;
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
        .orElseThrow(() -> new NotFoundException("Recipient is not found."));
    biddingNotification.setRecipient(recipient);

    biddingNotification.setIsRead(false);

    BiddingDocument relatedResource = biddingDocumentRepository.findById(request.getRelatedResource())
        .orElseThrow(() -> new NotFoundException("Related resource is not found."));
    biddingNotification.setRelatedResource(relatedResource);
    
    biddingNotification.setMessage(request.getMessage()); 
    EnumBiddingNotificationType type = EnumBiddingNotificationType.findByName(request.getType());
    biddingNotification.setType(type.name());
    
    biddingNotificationRepository.save(biddingNotification);
    return biddingNotification;
  }

  @Override
  public BiddingNotification getBiddingNotification(Long id) {
    BiddingNotification biddingNotification = biddingNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Bidding notification is not found."));
    return biddingNotification;
  }

  @Override
  public Page<BiddingNotification> getBiddingNotifications(PaginationRequest request) {
    Page<BiddingNotification> biddingNotifications = biddingNotificationRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return biddingNotifications;
  }

  @Override
  public Page<BiddingNotification> getBiddingNotificationsByUser(Long id, PaginationRequest request) {
    Page<BiddingNotification> biddingNotifications = biddingNotificationRepository.findBiddingNotificationsByUser(id,
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return biddingNotifications;
  }
  


  @Override
  public Page<BiddingNotification> getBiddingNotificationsByUser(String recipient, PaginationRequest request) {
    Page<BiddingNotification> biddingNotifications = biddingNotificationRepository.findBiddingNotificationsByUser(recipient,
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return biddingNotifications;
  }

  @Override
  public BiddingNotification editBiddingNotification(Long id, Map<String, Object> updates) {
    BiddingNotification biddingNotification = biddingNotificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Bidding notification is not found."));
    
    Boolean isRead = (Boolean)updates.get("isRead");
    if(isRead != null) {
      biddingNotification.setIsRead(isRead);
    }else {
      throw new NotFoundException("Is Read is not found.");
    }
    
    return biddingNotification;
  }

  @Override
  public void removeBiddingNotification(Long id) {
    if(biddingNotificationRepository.existsById(id)) {
      biddingNotificationRepository.deleteById(id);
    }else {
      throw new NotFoundException("Bidding notification is not found.");
    }
  }
}
