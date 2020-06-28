package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerDto {

  private Long id;

  private String driver;

  private String trailer;

  private String tractor;

  private String containerNumber;

  private String licensePlate;

  private String status;
}
