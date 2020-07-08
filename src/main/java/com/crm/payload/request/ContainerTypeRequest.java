package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerTypeRequest {

  private Long id;

  @NotBlank
  private String name;

  private String description;

  private Double tareWeight;

  private Double payloadCapacity;

  private Double cubicCapacity;

  private Double internalLength;

  private Double internalWidth;

  private Double internalHeight;

  private Double doorOpeningWidth;

  private Double doorOpeningHeight;

  private String unitOfMeasurement;
}
