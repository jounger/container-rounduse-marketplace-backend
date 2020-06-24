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

  @NotBlank
  private String description;

  @NotBlank
  private float tareWeight;

  @NotBlank
  private float payloadCapacity;

  @NotBlank
  private float cubicCapacity;

  @NotBlank
  private float internalLength;

  @NotBlank
  private float internalWidth;

  @NotBlank
  private float internalHeight;

  @NotBlank
  private float doorOpeningWidth;

  @NotBlank
  private float doorOpeningHeight;
}
