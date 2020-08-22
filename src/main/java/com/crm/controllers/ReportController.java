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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.enums.EnumReportStatus;
import com.crm.models.Report;
import com.crm.models.dto.ReportDto;
import com.crm.models.mapper.ReportMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ReportService;
import com.crm.websocket.controller.NotificationBroadcast;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/report")
public class ReportController {

  private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

  @Autowired
  private ReportService reportService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createReport(@Valid @RequestBody ReportRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Report report = reportService.createReport(username, request);
    ReportDto reportDto = ReportMapper.toReportDto(report);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastCreateReportToModerator(report);
    // END NOTIFICATION

    // Set default response body
    DefaultResponse<ReportDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_REPORT_SUCCESSFULLY);
    defaultResponse.setData(reportDto);

    logger.info("User {} createReport with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getReport(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Report report = reportService.getReport(id, username);
    ReportDto reportDto = ReportMapper.toReportDto(report);
    return ResponseEntity.ok(reportDto);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getReportsByUser(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Report> pages = reportService.getReportsByUser(username, request);

    PaginationResponse<ReportDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Report> reports = pages.getContent();
    List<ReportDto> reportsDto = new ArrayList<>();
    reports.forEach(report -> reportsDto.add(ReportMapper.toReportDto(report)));
    response.setContents(reportsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchReports(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<Report> pages = reportService.searchReports(request, search);
    PaginationResponse<ReportDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Report> reports = pages.getContent();
    List<ReportDto> reportsDto = new ArrayList<>();
    reports.forEach(report -> reportsDto.add(ReportMapper.toReportDto(report)));
    response.setContents(reportsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @GetMapping("")
  public ResponseEntity<?> getReports(@Valid PaginationRequest request) {
    Page<Report> pages = reportService.getReports(request);
    PaginationResponse<ReportDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Report> reports = pages.getContent();
    List<ReportDto> reportsDto = new ArrayList<>();
    reports.forEach(report -> reportsDto.add(ReportMapper.toReportDto(report)));
    response.setContents(reportsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editReport(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Report report = reportService.getReport(id, username);
    String status = report.getStatus();

    Report editReport = reportService.editReport(id, username, updates);
    ReportDto reportDto = ReportMapper.toReportDto(editReport);

    // CREATE NOTIFICATION
    if (status.equals(report.getStatus()) || report.getStatus().equals(EnumReportStatus.RESOLVED.name())) {
      notificationBroadcast.broadcastUpdateReportToModerator(editReport);
    } else {
      notificationBroadcast.broadcastUpdateReportToForwarder(editReport);
    }
    // END NOTIFICATION

    // Set default response body
    DefaultResponse<ReportDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_REPORT_SUCCESSFULLY);
    defaultResponse.setData(reportDto);

    logger.info("User {} editReport from id {} with request: {}", username, id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteReport(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    reportService.removeReport(id, username);

    // Set default response body
    DefaultResponse<ReportDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_REPORT_SUCCESSFULLY);

    logger.info("User {} deleteReport with id: {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
