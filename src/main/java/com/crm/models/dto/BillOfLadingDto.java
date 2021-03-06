package com.crm.models.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillOfLadingDto {

  private Long id;

  private PortDto portOfDelivery;

  private String number;

  private String freeTime;

  private Integer unit;

  private Set<ContainerDto> containers = new HashSet<>();
}
