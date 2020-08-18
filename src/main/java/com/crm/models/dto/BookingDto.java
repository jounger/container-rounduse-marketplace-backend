package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDto {

  private Long id;

  private PortDto portOfLoading;

  private String number;

  private Integer unit;

  private String cutOffTime;

  private Boolean isFcl;
}
