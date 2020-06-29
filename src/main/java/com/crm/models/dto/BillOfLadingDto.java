package com.crm.models.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillOfLadingDto {

  private Long id;

  private String portOfDelivery;

  private String billOfLadingNumber;

  private String freeTime;

  private Set<ContainerDto> containers = new HashSet<>();
}
