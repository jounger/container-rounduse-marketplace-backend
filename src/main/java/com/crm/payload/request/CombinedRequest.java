package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedRequest {
  
  private Long id;
  
  @NotBlank
  private Long biddingDocument;
  
  @NotBlank
  private String status;
}
