package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContainerSemiTrailerRequest extends VehicleRequest {

  private String type;

  private String unitOfMeasurement;
}
