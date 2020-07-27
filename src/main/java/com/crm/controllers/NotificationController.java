package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Notification;
import com.crm.models.dto.NotificationDto;
import com.crm.models.mapper.NotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.NotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

  @Autowired
  NotificationService notificationService;

  @GetMapping("/filter")
  public ResponseEntity<?> searchNotifications(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {

    Page<Notification> pages = notificationService.searchNotifications(request, search);
    PaginationResponse<NotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Notification> notifications = pages.getContent();
    List<NotificationDto> notificationDto = new ArrayList<>();
    notifications.forEach(notification -> notificationDto.add(NotificationMapper.toNotificationDto(notification)));
    response.setContents(notificationDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("/user")
  public ResponseEntity<?> getNotificationsByUser(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Page<Notification> pages = notificationService.getNotificationsByUser(username, request);
    PaginationResponse<NotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Notification> notifications = pages.getContent();
    List<NotificationDto> notificationDto = new ArrayList<>();
    notifications.forEach(notification -> notificationDto.add(NotificationMapper.toNotificationDto(notification)));
    response.setContents(notificationDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editNotification(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Notification notification = notificationService.editNotification(id, updates);
    NotificationDto notificationDto = NotificationMapper.toNotificationDto(notification);
    return ResponseEntity.ok(notificationDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
    notificationService.removeNotification(id);
    return ResponseEntity.ok(new MessageResponse("Notification deleted successfully."));
  }
}
