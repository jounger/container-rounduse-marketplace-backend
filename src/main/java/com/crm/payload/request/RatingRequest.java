package com.crm.payload.request;

import lombok.Setter;

import lombok.Getter;

@Getter
@Setter
public class RatingRequest {
  
  private Long id;
  
  private Long sender;
  
  private Long receiver;
  
  private Long contract;
  
  private Integer ratingValue;
}
