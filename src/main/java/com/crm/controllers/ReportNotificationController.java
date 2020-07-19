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

import com.crm.models.ReportNotification;
import com.crm.models.dto.ReportNotificationDto;
import com.crm.models.mapper.ReportNotificationMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ReportNotificationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/report-notification")
public class ReportNotificationController {

  @Autowired
  ReportNotificationService reportNotificationService;

  @GetMapping("/user/{id}")
  public ResponseEntity<?> getReportNotificationsByUser(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<ReportNotification> pages = reportNotificationService.getReportNotificationsByUser(id, request);

    PaginationResponse<ReportNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ReportNotification> reportNotifications = pages.getContent();
    List<ReportNotificationDto> reportNotificationsDto = new ArrayList<>();
    reportNotifications.forEach(reportNotification -> reportNotificationsDto
        .add(ReportNotificationMapper.toReportNotificationDto(reportNotification)));
    response.setContents(reportNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("")
  public ResponseEntity<?> getReportNotificationsByUser(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<ReportNotification> pages = reportNotificationService.getReportNotificationsByUsername(username, request);

    PaginationResponse<ReportNotificationDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ReportNotification> reportNotifications = pages.getContent();
    List<ReportNotificationDto> reportNotificationsDto = new ArrayList<>();
    reportNotifications.forEach(reportNotification -> reportNotificationsDto
        .add(ReportNotificationMapper.toReportNotificationDto(reportNotification)));
    response.setContents(reportNotificationsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getReportNotification(@PathVariable Long id) {
    ReportNotification reportNotification = reportNotificationService.getReportNotification(id);
    ReportNotificationDto reportNotificationDto = ReportNotificationMapper.toReportNotificationDto(reportNotification);
    return ResponseEntity.ok(reportNotificationDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editReportNotification(@PathVariable("id") Long id,
      @RequestBody Map<String, Object> updates) {
    ReportNotification reportNotification = reportNotificationService.editReportNotification(id, updates);
    ReportNotificationDto reportNotificationDto = ReportNotificationMapper.toReportNotificationDto(reportNotification);
    return ResponseEntity.ok(reportNotificationDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteReportNotification(@PathVariable Long id) {
    reportNotificationService.removeReportNotification(id);
    return ResponseEntity.ok(new MessageResponse("Report Notification deleted successfully."));
  }

}
