package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.ReportCategory;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportCategoryRequest;

public interface ReportCategoryService {

  Page<ReportCategory> getReportCategories(PaginationRequest request);

  ReportCategory getReportCategoryById(Long id);

  void createReportCategory(ReportCategoryRequest request);

  ReportCategory updateReportCategory(ReportCategoryRequest request);

  void removeReportCategory(Long id);

}
