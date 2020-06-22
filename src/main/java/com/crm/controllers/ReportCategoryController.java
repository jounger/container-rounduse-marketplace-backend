package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.ReportCategory;
import com.crm.models.dto.ReportCategoryDto;
import com.crm.models.mapper.ReportCategoryMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportCategoryRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ReportCategoryService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/report-category")
public class ReportCategoryController {

  @Autowired
  private ReportCategoryService reportCategoryService;

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getReportCategories(@Valid PaginationRequest request) {

    Page<ReportCategory> pages = reportCategoryService.getReportCategories(request);
    PaginationResponse<ReportCategoryDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ReportCategory> reportCategories = pages.getContent();
    List<ReportCategoryDto> reportCategoryDto = new ArrayList<>();
    reportCategories
        .forEach(reportCategory -> reportCategoryDto.add(ReportCategoryMapper.toReportCategoryDto(reportCategory)));
    response.setContents(reportCategoryDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getReportCategory(@PathVariable Long id) {

    ReportCategory reportCategory = reportCategoryService.getReportCategoryById(id);
    ReportCategoryDto reportCategoryDto = new ReportCategoryDto();
    reportCategoryDto = ReportCategoryMapper.toReportCategoryDto(reportCategory);
    return ResponseEntity.ok(reportCategoryDto);
  }

  @PostMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createReportCategory(@Valid @RequestBody ReportCategoryRequest request) {
    reportCategoryService.createReportCategory(request);
    return ResponseEntity.ok(new MessageResponse("ReportCategory created successfully"));
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> updateReportCategory(@Valid @RequestBody ReportCategoryRequest request) {
    ReportCategory reportCategory = reportCategoryService.updateReportCategory(request);
    ReportCategoryDto reportCategoryDto = new ReportCategoryDto();
    reportCategoryDto = ReportCategoryMapper.toReportCategoryDto(reportCategory);
    return ResponseEntity.ok(reportCategoryDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> removeReportCategory(@PathVariable Long id) {
    reportCategoryService.removeReportCategory(id);
    return ResponseEntity.ok(new MessageResponse("ReportCategory has remove successfully"));
  }

}
