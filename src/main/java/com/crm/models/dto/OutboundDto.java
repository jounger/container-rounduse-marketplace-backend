package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutboundDto extends SupplyDto {

  private String status;

  private BookingDto booking;

  private String goodsDescription;

  private String packingTime;

  private String packingStation;

  private Double payload;

  private String unitOfMeasurement;

}
