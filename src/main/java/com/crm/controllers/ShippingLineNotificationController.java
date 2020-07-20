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
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.ShippingLineNotification;
import com.crm.models.dto.ShippingLineNotificationDto;
import com.crm.models.mapper.ShippingLineNotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ShippingLineNotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/shipping-line-notification")
public class ShippingLineNotificationController {

  @Autowired
  ShippingLineNotificationService shippingLineNotificationService;
  
  @GetMapping("/user/{id}")
  public ResponseEntity<?> getShippingLineNotificationsByUser(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<ShippingLineNotification> pages = shippingLineNotificationService.getShippingLineNotificationsByUser(id, request);

    PaginationResponse<ShippingLineNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingLineNotification> shippingLineNotifications = pages.getContent();
    List<ShippingLineNotificationDto> shippingLineNotificationsDto = new ArrayList<>();
    shippingLineNotifications.forEach(shippingLineNotification -> shippingLineNotificationsDto
        .add(ShippingLineNotificationMapper.toShippingLineNotificationDto(shippingLineNotification)));
    response.setContents(shippingLineNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("")
  public ResponseEntity<?> getShippingLineNotificationsByUser(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<ShippingLineNotification> pages = shippingLineNotificationService.getShippingLineNotificationsByUsername(username, request);

    PaginationResponse<ShippingLineNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingLineNotification> shippingLineNotifications = pages.getContent();
    List<ShippingLineNotificationDto> shippingLineNotificationsDto = new ArrayList<>();
    shippingLineNotifications.forEach(shippingLineNotification -> shippingLineNotificationsDto
        .add(ShippingLineNotificationMapper.toShippingLineNotificationDto(shippingLineNotification)));
    response.setContents(shippingLineNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getShippingLineNotification(@PathVariable Long id) {
    ShippingLineNotification shippingLineNotification = shippingLineNotificationService.getShippingLineNotification(id);
    ShippingLineNotificationDto shippingLineNotificationDto = ShippingLineNotificationMapper.toShippingLineNotificationDto(shippingLineNotification);
    return ResponseEntity.ok(shippingLineNotificationDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editShippingLineNotification(@PathVariable("id") Long id,
      @RequestBody Map<String, Object> updates) {
    ShippingLineNotification shippingLineNotification = shippingLineNotificationService.editShippingLineNotification(id, updates);
    ShippingLineNotificationDto shippingLineNotificationDto = ShippingLineNotificationMapper.toShippingLineNotificationDto(shippingLineNotification);
    return ResponseEntity.ok(shippingLineNotificationDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteShippingLineNotification(@PathVariable Long id) {
    shippingLineNotificationService.removeShippingLineNotification(id);
    return ResponseEntity.ok(new MessageResponse("Driver Notification deleted successfully."));
  }
}