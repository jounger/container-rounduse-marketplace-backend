package com.crm.models.mapper;

import com.crm.models.Feedback;
import com.crm.models.User;
import com.crm.models.dto.FeedbackDto;

public class FeedbackMapper {

  public static FeedbackDto toFeedbackDto(Feedback feedback) {
    FeedbackDto feedbackDto = new FeedbackDto();

    feedbackDto.setId(feedback.getId());
    feedbackDto.setReport(feedback.getReport().getId());

    User sender = feedback.getSender();
    feedbackDto.setSender(sender.getUsername());

    feedbackDto.setMessage(feedback.getMessage());
    feedbackDto.setSatisfactionPoints(feedback.getSatisfactionPoints());

    return feedbackDto;
  }
}
