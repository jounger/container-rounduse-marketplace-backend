package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerTypeDto {
  
  private Long id;
  
  private String name;

  private String description;

  private float tareWeight;

  private float payloadCapacity;

  private float cubicCapacity;

  private float internalLength;

  private float internalWidth;

  private float internalHeight;

  private float doorOpeningWidth;

  private float doorOpeningHeight;
}
