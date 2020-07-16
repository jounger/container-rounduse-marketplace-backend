package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
  private Long id;
  
  private String sender;
  
  private String receiver;
  
  private ContractDto contract;
  
  private Integer ratingValue;
}
