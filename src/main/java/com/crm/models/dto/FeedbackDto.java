package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {

  private Long id;

  private UserDto sender;

  private UserDto recipient;

  private String message;

  private Integer satisfactionPoints;

  private String sendDate;
}
