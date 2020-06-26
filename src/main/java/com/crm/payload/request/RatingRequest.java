package com.crm.payload.request;

import lombok.Setter;

import lombok.Getter;

@Getter
@Setter
public class RatingRequest {
  
  private Long id;
  
  private Long senderId;
  
  private Long receiverId;
  
  private Integer ratingValue;
}
