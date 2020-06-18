package com.crm.payload.request;

import java.util.Set;

import com.crm.models.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsignmentRequest {
  
  @JsonProperty("shipping_line_name")
  private String shippingLineName;
  
  @JsonProperty("container_type")
  private String containerType;
  
  private String status;
  
  @JsonProperty("packing_time")
  private String packingTime;
  
  @JsonProperty("packing_station")
  private Address packingStation;
  
  @JsonProperty("booking_number")
  private String bookingNumber;
  
  private String laytime;
  
  @JsonProperty("cut_of_time")
  private String cutOfTime;
  
  private float payload;
  
  @JsonProperty("unit_of_measurement")
  private float unitOfMeasurement;
  
  private Set<String> categories;
  
  @JsonProperty("port_of_loading")
  private String portOfLoading; 
  
  
}
