package com.crm.models.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerDto {
  
  private String shippingLine;
  
  private String containerType;
  
  private String status;
  
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
  
  private Set<Long> bids;
}
