package com.crm.payload.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillOfLadingRequest {

  private Long id;

  @NotBlank
  private String portOfDelivery;

  @NotBlank
  private String billOfLadingNumber;

  private Integer freeTime;

  private List<ContainerRequest> containers = new ArrayList<ContainerRequest>();
}
