package com.crm.services.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.exception.NotFoundException;
import com.crm.models.Notification;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.NotificationRepository;
import com.crm.services.NotificationService;
import com.crm.specification.builder.NotificationSpecificationsBuilder;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  NotificationRepository notificationRepository;

  @Override
  public Page<Notification> searchNotifications(PaginationRequest request, String search) {
    NotificationSpecificationsBuilder builder = new NotificationSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Notification> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Notification> pages = notificationRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Page<Notification> getNotificationsByUser(Long recipient, PaginationRequest request) {
    String status = request.getStatus();
    Page<Notification> notifications = null;
    if (status != null && !status.isEmpty()) {
      notifications = notificationRepository.findByUserAndStatus(recipient, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      notifications = notificationRepository.findByUser(recipient,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return notifications;
  }

  @Override
  public Notification editNotification(Long id, Map<String, Object> updates) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Notification is not found."));

    Boolean isRead = (Boolean) updates.get("isRead");
    if (isRead != null) {
      notification.setIsRead(isRead);
    } else {
      throw new NotFoundException("Is Read is not found.");
    }

    Boolean isHide = (Boolean) updates.get("isHide");
    if (isHide != null) {
      notification.setIsHide(isHide);
    } else {
      throw new NotFoundException("Is Hide is not found.");
    }

    notificationRepository.save(notification);
    return notification;
  }

  @Override
  public void removeNotification(Long id) {
    if (notificationRepository.existsById(id)) {
      notificationRepository.deleteById(id);
    } else {
      throw new NotFoundException("Notification is not found.");
    }
  }

}
