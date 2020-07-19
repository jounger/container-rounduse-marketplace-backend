package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.ReportNotification;
import com.crm.models.dto.ReportDto;
import com.crm.models.dto.ReportNotificationDto;

public class ReportNotificationMapper {

  public static ReportNotificationDto toReportNotificationDto(ReportNotification reportNotification) {
    ReportNotificationDto reportNotificationDto = new ReportNotificationDto();

    reportNotificationDto.setId(reportNotification.getId());
    reportNotificationDto.setRecipient(reportNotification.getRecipient().getUsername());
    reportNotificationDto.setIsRead(reportNotification.getIsRead());

    ReportDto relatedResource = ReportMapper.toReportDto(reportNotification.getRelatedResource());
    reportNotificationDto.setRelatedResource(relatedResource);

    reportNotificationDto.setMessage(reportNotification.getMessage());
    reportNotificationDto.setAction(reportNotification.getAction());
    reportNotificationDto.setType(reportNotification.getType());

    reportNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(reportNotification.getSendDate()));

    return reportNotificationDto;
  }
}
