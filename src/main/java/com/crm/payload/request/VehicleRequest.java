package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VehicleRequest {

  private Long id;

  private String licensePlate;

  private Integer numberOfAxles;
}
