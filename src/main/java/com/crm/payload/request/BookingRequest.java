package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

  private Long id;

  @NotBlank
  private String portOfLoading;

  @NotBlank
  private String number;
  
  @NotNull
  private Integer unit;
  
  @NotBlank
  private String cutOffTime;
  
  private Boolean isFcl;
}
