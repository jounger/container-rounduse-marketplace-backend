package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.ReportCategory;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportCategoryRequest;
import com.crm.repository.ReportCategoryRepository;
import com.crm.services.ReportCategoryService;

@Service
public class ReportCategoryServiceImpl implements ReportCategoryService {

  @Autowired
  private ReportCategoryRepository reportCategoryRepository;

  @Override
  public Page<ReportCategory> getReportCategories(PaginationRequest request) {

    Page<ReportCategory> pages = reportCategoryRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public ReportCategory getReportCategoryById(Long id) {

    ReportCategory reportCategory = reportCategoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: ReportCategory is not found."));
    return reportCategory;
  }

  @Override
  public void createReportCategory(ReportCategoryRequest request) {

    ReportCategory reportCategory = new ReportCategory();
    if (reportCategoryRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("Error: ReportCategory has been existed");
    }
    reportCategory.setName(request.getName());
    reportCategory.setDescription(request.getDescription());
    reportCategoryRepository.save(reportCategory);
  }

  @Override
  public ReportCategory updateReportCategory(ReportCategoryRequest request) {
    ReportCategory reportCategory = reportCategoryRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: ReportCategory is not found."));

    reportCategory.setName(request.getName());
    reportCategory.setDescription(request.getDescription());
    reportCategoryRepository.save(reportCategory);

    return reportCategory;
  }

  @Override
  public void removeReportCategory(Long id) {
    if (reportCategoryRepository.existsById(id)) {
      reportCategoryRepository.deleteById(id);
    } else {
      throw new NotFoundException("Error: ReportCategory is not found.");
    }

  }

}
