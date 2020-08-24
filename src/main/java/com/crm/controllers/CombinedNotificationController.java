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
import com.crm.models.CombinedNotification;
import com.crm.models.dto.CombinedNotificationDto;
import com.crm.models.mapper.CombinedNotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.CombinedNotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('SHIPPINGLINE')")
@RestController
@RequestMapping("/api/combined-notification")
public class CombinedNotificationController {

  private static final Logger logger = LoggerFactory.getLogger(CombinedNotificationController.class);

  @Autowired
  CombinedNotificationService combinedNotificationService;

  @GetMapping("")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER') or hasRole('SHIPPINGLINE')")
  public ResponseEntity<?> getCombinedNotifications(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<CombinedNotification> pages = combinedNotificationService.getCombinedNotificationsByUsername(username,
        request);

    PaginationResponse<CombinedNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<CombinedNotification> combinedNotifications = pages.getContent();
    List<CombinedNotificationDto> combinedNotificationsDto = new ArrayList<>();
    combinedNotifications.forEach(combinedNotification -> combinedNotificationsDto
        .add(CombinedNotificationMapper.toCombinedNotificationDto(combinedNotification)));
    response.setContents(combinedNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER') or hasRole('SHIPPINGLINE')")
  public ResponseEntity<?> getCombinedNotification(@PathVariable Long id) {
    CombinedNotification combinedNotification = combinedNotificationService.getCombinedNotification(id);
    CombinedNotificationDto combinedNotificationDto = CombinedNotificationMapper
        .toCombinedNotificationDto(combinedNotification);
    return ResponseEntity.ok(combinedNotificationDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER') or hasRole('SHIPPINGLINE')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editCombinedNotification(@PathVariable("id") Long id,
      @RequestBody Map<String, Object> updates) {
    CombinedNotification combinedNotification = combinedNotificationService.editCombinedNotification(id,
        updates);
    CombinedNotificationDto combinedNotificationDto = CombinedNotificationMapper
        .toCombinedNotificationDto(combinedNotification);

    // Set default response body
    DefaultResponse<CombinedNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(Constant.EMPTY_STRING);
    defaultResponse.setData(combinedNotificationDto);

    logger.info("editCombinedNotification from id {} with request {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER') or hasRole('SHIPPINGLINE')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCombinedNotification(@PathVariable Long id) {
    combinedNotificationService.removeCombinedNotification(id);

    // Set default response body
    DefaultResponse<CombinedNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_NOTIFICATION_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
