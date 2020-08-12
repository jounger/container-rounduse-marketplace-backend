package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Feedback;
import com.crm.models.dto.FeedbackDto;
import com.crm.models.mapper.FeedbackMapper;
import com.crm.payload.request.FeedbackRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.FeedbackService;
import com.crm.websocket.controller.NotificationBroadcast;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

  @Autowired
  private FeedbackService paymentService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Transactional
  @PostMapping("/report/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createFeedback(@PathVariable("id") Long id, @Valid @RequestBody FeedbackRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();
    Feedback payment = paymentService.createFeedback(id, username, request);
    FeedbackDto feedbackDto = FeedbackMapper.toFeedbackDto(payment);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastCreateFeedbackToUser(payment);
    // END NOTIFICATION

    // Set default response body
    DefaultResponse<FeedbackDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_FEEDBACK_SUCCESSFULLY);
    defaultResponse.setData(feedbackDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @Transactional
  @RequestMapping(method = RequestMethod.POST, params = { "id", "name" })
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createFeedbackToModerator(@RequestParam Long id, @RequestParam String name,
      @Valid @RequestBody FeedbackRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();
    Feedback payment = paymentService.createFeedback(id, username, request);
    FeedbackDto paymentDto = FeedbackMapper.toFeedbackDto(payment);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastCreateFeedbackToModerator(name, payment);
    // END NOTIFICATION

    return ResponseEntity.ok(paymentDto);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @GetMapping("/report/{id}")
  public ResponseEntity<?> getFeedbacksByReport(@PathVariable Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Page<Feedback> pages = paymentService.getFeedbacksByReport(id, username, request);

    PaginationResponse<FeedbackDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Feedback> payments = pages.getContent();
    List<FeedbackDto> paymentsDto = new ArrayList<>();
    payments.forEach(payment -> paymentsDto.add(FeedbackMapper.toFeedbackDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getFeedbacksByUser(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Feedback> pages = paymentService.getFeedbacksByUser(username, request);

    PaginationResponse<FeedbackDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Feedback> payments = pages.getContent();
    List<FeedbackDto> paymentsDto = new ArrayList<>();
    payments.forEach(payment -> paymentsDto.add(FeedbackMapper.toFeedbackDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchFeedbacks(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<Feedback> pages = paymentService.searchFeedbacks(request, search);
    PaginationResponse<FeedbackDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Feedback> payments = pages.getContent();
    List<FeedbackDto> paymentsDto = new ArrayList<>();
    payments.forEach(payment -> paymentsDto.add(FeedbackMapper.toFeedbackDto(payment)));
    response.setContents(paymentsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editFeedback(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();
    Feedback payment = paymentService.editFeedback(id, username, updates);
    FeedbackDto feedbackDto = FeedbackMapper.toFeedbackDto(payment);

    // Set default response body
    DefaultResponse<FeedbackDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_FEEDBACK_SUCCESSFULLY);
    defaultResponse.setData(feedbackDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();
    paymentService.removeFeedback(id, username);

    // Set default response body
    DefaultResponse<FeedbackDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_FEEDBACK_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(defaultResponse);
  }
}
