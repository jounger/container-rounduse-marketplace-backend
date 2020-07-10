package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Notification;
import com.crm.models.dto.NotificationDto;
import com.crm.models.mapper.NotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.NotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container-type")
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
}
