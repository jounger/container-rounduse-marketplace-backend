package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OutboundRequest extends SupplyRequest {

  private BookingRequest booking;

  private String goodsDescription;

  private String packingTime;

  private String packingStation;

  private Double grossWeight;

  private String deliveryTime;

  private String unitOfMeasurement;

  private String status;
}
