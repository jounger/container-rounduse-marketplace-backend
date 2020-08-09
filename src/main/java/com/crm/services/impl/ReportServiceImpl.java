package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumReportStatus;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BiddingDocument;
import com.crm.models.Report;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportRequest;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.ReportRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ReportService;
import com.crm.specification.builder.ReportSpecificationsBuilder;

@Service
public class ReportServiceImpl implements ReportService {

  @Autowired
  private ReportRepository reportRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Report createReport(String username, ReportRequest request) {
    Report report = new Report();
    Supplier sender = supplierRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
    report.setSender(sender);

    BiddingDocument reportBiddingDocument = biddingDocumentRepository.findById(request.getReport())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BIDDINGDOCUMENT_NOT_FOUND));
    report.setReport(reportBiddingDocument);

    String title = request.getTitle();
    if (!Tool.isBlank(title)) {
      report.setTitle(title);
    } else {
      throw new NotFoundException(ErrorConstant.REPORT_TITLE_INVALID_TIME);
    }

    String detail = request.getDetail();
    if (!Tool.isBlank(detail)) {
      report.setDetail(detail);
    } else {
      throw new NotFoundException(ErrorConstant.REPORT_DETAIL_INVALID_TIME);
    }

    report.setStatus(EnumReportStatus.PENDING.name());
    report.setSendDate(LocalDateTime.now());

    Report _report = reportRepository.save(report);
    return _report;
  }

  @Override
  public Report getReport(Long id, String username) {
    Report report = reportRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.REPORT_NOT_FOUND));
    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.USER_NOT_FOUND));
    String role = user.getRoles().iterator().next().getName();
    if (!report.getSender().getUsername().equals(username) && !role.equalsIgnoreCase("ROLE_MODERATOR")) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
    return report;
  }

  @Override
  public Page<Report> getReportsByUser(String username, PaginationRequest request) {
    Supplier sender = supplierRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Report> reports = reportRepository.findBySender(sender, page);
    return reports;
  }

  @Override
  public Page<Report> searchReports(PaginationRequest request, String search) {
    // Extract data from search string
    ReportSpecificationsBuilder builder = new ReportSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Report> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Report> pages = reportRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Page<Report> getReports(PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Report> reports = reportRepository.findAll(page);
    return reports;
  }

  @Override
  public Report editReport(Long id, String username, Map<String, Object> updates) {
    Report report = reportRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.REPORT_NOT_FOUND));

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Supplier is not found."));
    String role = user.getRoles().iterator().next().getName();
    if (report.getSender().getUsername().equals(username) || role.equals("ROLE_MODERATOR")) {

    } else {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (report.getSender().getUsername().equals(username)) {

      if (report.getStatus().equals(EnumReportStatus.RESOLVED.name())
          || report.getStatus().equals(EnumReportStatus.REJECTED.name())
          || report.getStatus().equals(EnumReportStatus.CLOSED.name())) {
        throw new InternalException(ErrorConstant.REPORT_INVALID_TIME);
      }
      String title = String.valueOf(updates.get("title"));
      if (updates.get("title") != null && !Tool.isEqual(report.getTitle(), title)) {
        report.setTitle(title);
      }

      String detail = String.valueOf(updates.get("detail"));
      if (updates.get("detail") != null && !Tool.isEqual(report.getDetail(), detail)) {
        report.setDetail(detail);
      }

      String statusString = String.valueOf(updates.get("status"));
      if (updates.get("status") != null && !Tool.isEqual(report.getStatus(), statusString)) {
        EnumReportStatus status = EnumReportStatus.findByName(statusString);
        if (status == null) {
          throw new NotFoundException(ErrorConstant.REPORT_STATUS_NOT_FOUND);
        }
        report.setStatus(status.name());
      }
    }

    if (role.equals("ROLE_MODERATOR")) {
      if (!report.getStatus().equals(EnumReportStatus.RESOLVED.name())) {
        throw new InternalException(ErrorConstant.REPORT_INVALID_TIME);
      }
      String statusString = String.valueOf(updates.get("status"));
      if (updates.get("status") != null && !Tool.isEqual(report.getStatus(), statusString)) {
        EnumReportStatus status = EnumReportStatus.findByName(statusString);
        if (status == null) {
          throw new NotFoundException(ErrorConstant.REPORT_STATUS_NOT_FOUND);
        }
        report.setStatus(status.name());
      }
    }

    Report _report = reportRepository.save(report);
    return _report;
  }

  @Override
  public void removeReport(Long id, String username) {
    Report report = reportRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.REPORT_NOT_FOUND));
    if (report.getSender().getUsername().equals(username)) {
      reportRepository.delete(report);
    } else {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
  }

}
