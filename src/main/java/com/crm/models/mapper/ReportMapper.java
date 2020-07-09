package com.crm.models.mapper;

import com.crm.models.Report;
import com.crm.models.dto.ReportDto;

public class ReportMapper {
  public static ReportDto toReportDto(Report report) {
    ReportDto reportDto = new ReportDto();

    reportDto.setId(report.getId());
    reportDto.setSender(report.getSender().getUsername());
    reportDto.setReport(report.getReport().getId());
    reportDto.setTitle(report.getTitle());
    reportDto.setDetail(report.getDetail());
    reportDto.setStatus(report.getStatus());

    return reportDto;
  }
}
