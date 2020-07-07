package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerSemiTrailerRequest extends VehicleRequest {

  private String type;

  private String unitOfMeasurement;
}
