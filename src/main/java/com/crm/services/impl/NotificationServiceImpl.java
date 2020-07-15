package com.crm.services.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.Notification;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.NotificationRepository;
import com.crm.services.NotificationService;
import com.crm.specification.builder.NotificationSpecificationsBuilder;

@Service
public class NotificationServiceImpl implements NotificationService{
  
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

}
