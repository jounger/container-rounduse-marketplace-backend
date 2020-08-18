package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Report;
import com.crm.models.dto.ReportDto;

public class ReportMapper {
  public static ReportDto toReportDto(Report report) {
    if (report == null) {
      return null;
    }

    ReportDto reportDto = new ReportDto();

    reportDto.setId(report.getId());
    reportDto.setSender(SupplierMapper.toSupplierDto(report.getSender()));
    reportDto.setReport(BiddingDocumentMapper.toBiddingDocumentDto(report.getReport()));
    reportDto.setTitle(report.getTitle());
    reportDto.setDetail(report.getDetail());
    reportDto.setStatus(report.getStatus());
    reportDto.setSendDate(Tool.convertLocalDateTimeToString(report.getSendDate()));

    return reportDto;
  }
}
