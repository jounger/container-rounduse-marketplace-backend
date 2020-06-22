package com.crm.models.mapper;

import com.crm.models.ReportCategory;
import com.crm.models.dto.ReportCategoryDto;

public class ReportCategoryMapper {
  public static ReportCategoryDto toReportCategoryDto(ReportCategory reportCategory) {
    ReportCategoryDto reportCategoryDto = new ReportCategoryDto();
    reportCategoryDto.setId(reportCategory.getId());
    reportCategoryDto.setName(reportCategory.getName());
    reportCategoryDto.setDescription(reportCategory.getDescription());

    return reportCategoryDto;
  }
}
