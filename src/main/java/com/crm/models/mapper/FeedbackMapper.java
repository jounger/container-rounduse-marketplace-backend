package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Feedback;
import com.crm.models.User;
import com.crm.models.dto.FeedbackDto;

public class FeedbackMapper {

  public static FeedbackDto toFeedbackDto(Feedback feedback) {
    if (feedback == null) {
      return null;
    }

    FeedbackDto feedbackDto = new FeedbackDto();

    feedbackDto.setId(feedback.getId());
    feedbackDto.setReport(ReportMapper.toReportDto(feedback.getReport()));

    User sender = feedback.getSender();
    feedbackDto.setSender(UserMapper.toUserDto(sender));
    User recipient = feedback.getRecipient();
    feedbackDto.setRecipient(UserMapper.toUserDto(recipient));

    feedbackDto.setMessage(feedback.getMessage());
    feedbackDto.setSatisfactionPoints(feedback.getSatisfactionPoints());
    feedbackDto.setSendDate(Tool.convertLocalDateTimeToString(feedback.getSendDate()));

    return feedbackDto;
  }
}
