package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerSemiTrailerDto extends VehicleDto {

  private String type;

  private String unitOfMeasurement;
}
