package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerDto {

  private Long id;

  private String driver;

  private ContainerSemiTrailerDto trailer;

  private ContainerTractorDto tractor;

  private String number;

  private String status;
}
