package com.crm.models.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerDto {
  
  private Long id;
  
  @JsonProperty("shipping_line")
  private String shippingLine;
  
  @JsonProperty("container_type")
  private String containerType;

  private String status;
  
  @JsonProperty("forwarder_id")
  private Long forwarderId;
  
  @JsonProperty("driver_id")
  private Long driverId;
  
  @JsonProperty("container_trailer")
  private String containerTrailer;

  @JsonProperty("container_tractor")
  private String containerTractor;

  @JsonProperty("container_number")
  private String containerNumber;

  @JsonProperty("bl_number")
  private String blNumber;

  @JsonProperty("license_plate")
  private String licensePlate;

  @JsonProperty("empty_time")
  private String emptyTime;

  @JsonProperty("pick_up_time")
  private String pickUpTime;
  
  @JsonProperty("return_station")
  private Map<String, String> returnStation = new HashMap<>();
  
  @JsonProperty("port_of_delivery")
  private String portOfDelivery;

  @JsonProperty("free_time")
  private int freeTime;
  
  @JsonProperty("bids")
  private Set<Long> bids;
}
