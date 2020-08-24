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
import com.crm.models.ShippingNotification;
import com.crm.models.dto.ShippingNotificationDto;
import com.crm.models.mapper.ShippingNotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ShippingNotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('DRIVER')")
@RestController
@RequestMapping("/api/shipping-notification")
public class ShippingNotificationController {

  private static final Logger logger = LoggerFactory.getLogger(ShippingNotificationController.class);

  @Autowired
  ShippingNotificationService shippingNotificationService;

  @GetMapping("")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('DRIVER')")
  public ResponseEntity<?> getShippingNotifications(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<ShippingNotification> pages = shippingNotificationService.getShippingNotificationsByUsername(username, request);

    PaginationResponse<ShippingNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingNotification> shippingNotifications = pages.getContent();
    List<ShippingNotificationDto> shippingNotificationsDto = new ArrayList<>();
    shippingNotifications.forEach(shippingNotification -> shippingNotificationsDto
        .add(ShippingNotificationMapper.toShippingNotificationDto(shippingNotification)));
    response.setContents(shippingNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('DRIVER')")
  public ResponseEntity<?> getShippingNotification(@PathVariable Long id) {
    ShippingNotification shippingNotification = shippingNotificationService.getShippingNotification(id);
    ShippingNotificationDto shippingNotificationDto = ShippingNotificationMapper
        .toShippingNotificationDto(shippingNotification);
    return ResponseEntity.ok(shippingNotificationDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editShippingNotification(@PathVariable("id") Long id,
      @RequestBody Map<String, Object> updates) {
    ShippingNotification shippingNotification = shippingNotificationService.editShippingNotification(id, updates);
    ShippingNotificationDto shippingNotificationDto = ShippingNotificationMapper
        .toShippingNotificationDto(shippingNotification);

    // Set default response body
    DefaultResponse<ShippingNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(Constant.EMPTY_STRING);
    defaultResponse.setData(shippingNotificationDto);

    logger.info("editShippingNotification from id {} with request: {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('DRIVER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteShippingNotification(@PathVariable Long id) {
    shippingNotificationService.removeShippingNotification(id);

    // Set default response body
    DefaultResponse<ShippingNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_NOTIFICATION_SUCCESSFULLY);

    logger.info("deleteShippingNotification from report id {}", id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
