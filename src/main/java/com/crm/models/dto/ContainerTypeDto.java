package com.crm.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerTypeDto {
  
  private Long id;
  
  private String name;

  private String description;

  @JsonProperty("tare_weight")
  private float tareWeight;

  @JsonProperty("payload_capacity")
  private float payloadCapacity;

  @JsonProperty("cubic_capacity")
  private float cubicCapacity;

  @JsonProperty("internal_length")
  private float internalLength;

  @JsonProperty("internal-width")
  private float internalWidth;

  @JsonProperty("internal_height")
  private float internalHeight;

  @JsonProperty("door_opening_width")
  private float doorOpeningWidth;

  @JsonProperty("door_opening_height")
  private float doorOpeningHeight;
}
