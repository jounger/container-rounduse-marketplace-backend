package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Report;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportRequest;

public interface ReportService {

Report createReport(String username, ReportRequest request);
  
  Page<Report> getReportsByUser(String username, PaginationRequest request);
  
  Page<Report> searchReports(PaginationRequest request, String search);
  
  Report editReport(Long id, String username, Map<String, Object> updates);
  
  void removeReport(Long id, String username);
}
