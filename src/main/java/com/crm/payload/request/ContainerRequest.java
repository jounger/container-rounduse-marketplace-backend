package com.crm.payload.request;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContainerRequest {

  private Long id;

  private String number;

  // driver as Driver.userName
  private String driver;

  private String trailer;

  private String tractor;

  // ESupplyStatus
  private String status;

  private Set<Long> bids;
}
