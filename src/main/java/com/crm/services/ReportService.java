package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Report;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportRequest;

public interface ReportService {

  Report createReport(String username, ReportRequest request);

  Report getReport(Long id, String username);

  Page<Report> getReportsByUser(String username, PaginationRequest request);

  Page<Report> searchReports(PaginationRequest request, String search);

  Page<Report> getReports(PaginationRequest request);

  Report editReport(Long id, String username, Map<String, Object> updates);

  void removeReport(Long id, String username);
}
