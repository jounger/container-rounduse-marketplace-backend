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
import com.crm.models.ShippingLineNotification;
import com.crm.models.dto.ShippingLineNotificationDto;
import com.crm.models.mapper.ShippingLineNotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ShippingLineNotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('SHIPPINGLINE')")
@RestController
@RequestMapping("/api/shipping-line-notification")
public class ShippingLineNotificationController {

  private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

  @Autowired
  ShippingLineNotificationService shippingLineNotificationService;

  @GetMapping("")
  public ResponseEntity<?> getShippingLineNotifications(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<ShippingLineNotification> pages = shippingLineNotificationService
        .getShippingLineNotificationsByUsername(username, request);

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
    ShippingLineNotificationDto shippingLineNotificationDto = ShippingLineNotificationMapper
        .toShippingLineNotificationDto(shippingLineNotification);
    return ResponseEntity.ok(shippingLineNotificationDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editShippingLineNotification(@PathVariable("id") Long id,
      @RequestBody Map<String, Object> updates) {
    ShippingLineNotification shippingLineNotification = shippingLineNotificationService.editShippingLineNotification(id,
        updates);
    ShippingLineNotificationDto shippingLineNotificationDto = ShippingLineNotificationMapper
        .toShippingLineNotificationDto(shippingLineNotification);

    // Set default response body
    DefaultResponse<ShippingLineNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(Constant.EMPTY_STRING);
    defaultResponse.setData(shippingLineNotificationDto);

    logger.info("editShippingLineNotification from id {} with request {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteShippingLineNotification(@PathVariable Long id) {
    shippingLineNotificationService.removeShippingLineNotification(id);

    // Set default response body
    DefaultResponse<ShippingLineNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_NOTIFICATION_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
