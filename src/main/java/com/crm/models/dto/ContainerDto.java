package com.crm.models.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerDto extends SupplyDto{
  
  private Long forwarderId;
  
  private Long driverId;
  
  private String containerTrailer;

  private String containerTractor;

  private String containerNumber;

  private String blNumber;

  private String licensePlate;

  private String emptyTime;

  private String pickUpTime;
  
  private Map<String, String> returnStation = new HashMap<>();
  
  private String portOfDelivery;

  private int freeTime;
  
}
