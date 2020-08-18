package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.Constant;
import com.crm.common.SuccessMessage;
import com.crm.models.DriverNotification;
import com.crm.models.dto.DriverNotificationDto;
import com.crm.models.mapper.DriverNotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.DriverNotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('DRIVER')")
@RestController
@RequestMapping("/api/driver-notification")
public class DriverNotificationController {

  private static final Logger logger = LoggerFactory.getLogger(DriverNotificationController.class);

  @Autowired
  DriverNotificationService driverNotificationService;

  @GetMapping("/user/{id}")
  public ResponseEntity<?> getDriverNotificationsByUser(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<DriverNotification> pages = driverNotificationService.getDriverNotificationsByUser(id, request);

    PaginationResponse<DriverNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<DriverNotification> driverNotifications = pages.getContent();
    List<DriverNotificationDto> driverNotificationsDto = new ArrayList<>();
    driverNotifications.forEach(driverNotification -> driverNotificationsDto
        .add(DriverNotificationMapper.toDriverNotificationDto(driverNotification)));
    response.setContents(driverNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("")
  public ResponseEntity<?> getDriverNotificationsByUser(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<DriverNotification> pages = driverNotificationService.getDriverNotificationsByUsername(username, request);

    PaginationResponse<DriverNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<DriverNotification> driverNotifications = pages.getContent();
    List<DriverNotificationDto> driverNotificationsDto = new ArrayList<>();
    driverNotifications.forEach(driverNotification -> driverNotificationsDto
        .add(DriverNotificationMapper.toDriverNotificationDto(driverNotification)));
    response.setContents(driverNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getDriverNotification(@PathVariable Long id) {
    DriverNotification driverNotification = driverNotificationService.getDriverNotification(id);
    DriverNotificationDto driverNotificationDto = DriverNotificationMapper.toDriverNotificationDto(driverNotification);
    return ResponseEntity.ok(driverNotificationDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editDriverNotification(@PathVariable("id") Long id,
      @RequestBody Map<String, Object> updates) {
    DriverNotification driverNotification = driverNotificationService.editDriverNotification(id, updates);
    DriverNotificationDto driverNotificationDto = DriverNotificationMapper.toDriverNotificationDto(driverNotification);

    // Set default response body
    DefaultResponse<DriverNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(Constant.EMPTY_STRING);
    defaultResponse.setData(driverNotificationDto);

    logger.info("editDriverNotification from id {} with request: {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteDriverNotification(@PathVariable Long id) {
    driverNotificationService.removeDriverNotification(id);

    // Set default response body
    DefaultResponse<DriverNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_NOTIFICATION_SUCCESSFULLY);

    logger.info("deleteDriverNotification from report id {}", id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
