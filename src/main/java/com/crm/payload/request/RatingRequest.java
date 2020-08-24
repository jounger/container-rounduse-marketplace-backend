package com.crm.payload.request;

import lombok.Setter;
import lombok.ToString;
import lombok.Getter;

@Getter
@Setter
@ToString
public class RatingRequest {

  private Long id;

  private Integer ratingValue;

  private String comment;
}
