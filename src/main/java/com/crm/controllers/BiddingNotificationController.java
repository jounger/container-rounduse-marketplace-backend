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
import com.crm.models.BiddingNotification;
import com.crm.models.dto.BiddingNotificationDto;
import com.crm.models.mapper.BiddingNotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BiddingNotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
@RestController
@RequestMapping("/api/bidding-notification")
public class BiddingNotificationController {

  private static final Logger logger = LoggerFactory.getLogger(BiddingNotificationController.class);

  @Autowired
  private BiddingNotificationService biddingNotificationService;

  /*
   * @PostMapping("") public ResponseEntity<?>
   * createBiddingNotification(@Valid @RequestBody BiddingNotificationRequest
   * request) { BiddingNotification biddingNotification =
   * biddingNotificationService.createBiddingNotification(request);
   * BiddingNotificationDto biddingNotificationDto = BiddingNotificationMapper
   * .toBiddingNotificationDto(biddingNotification); return
   * ResponseEntity.ok(biddingNotificationDto); }
   */

  @GetMapping("")
  public ResponseEntity<?> getBiddingNotifications(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<BiddingNotification> pages = biddingNotificationService.getBiddingNotificationsByUser(username, request);

    PaginationResponse<BiddingNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<BiddingNotification> biddingNotifications = pages.getContent();
    List<BiddingNotificationDto> biddingNotificationsDto = new ArrayList<>();
    biddingNotifications.forEach(biddingNotification -> biddingNotificationsDto
        .add(BiddingNotificationMapper.toBiddingNotificationDto(biddingNotification)));
    response.setContents(biddingNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getBiddingNotification(@PathVariable Long id) {
    BiddingNotification biddingNotification = biddingNotificationService.getBiddingNotification(id);
    BiddingNotificationDto biddingNotificationDto = BiddingNotificationMapper
        .toBiddingNotificationDto(biddingNotification);
    return ResponseEntity.ok(biddingNotificationDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editBiddingNotification(@PathVariable("id") Long id,
      @RequestBody Map<String, Object> updates) {
    BiddingNotification biddingNotification = biddingNotificationService.editBiddingNotification(id, updates);
    BiddingNotificationDto biddingNotificationDto = BiddingNotificationMapper
        .toBiddingNotificationDto(biddingNotification);

    // Set default response body
    DefaultResponse<BiddingNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(Constant.EMPTY_STRING);
    defaultResponse.setData(biddingNotificationDto);

    logger.info("editBiddingNotification from id {} with request: {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBiddingNotification(@PathVariable Long id) {
    biddingNotificationService.removeBiddingNotification(id);

    // Set default response body
    DefaultResponse<BiddingNotificationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_NOTIFICATION_SUCCESSFULLY);

    logger.info("deleteBiddingNotification id {}", id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

}
