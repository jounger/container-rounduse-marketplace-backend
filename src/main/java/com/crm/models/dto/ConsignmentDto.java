package com.crm.models.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsignmentDto {
  
  private Long id;
  
  @JsonProperty("shipping_line")
  private String shippingLine;
  
  @JsonProperty("container_type")
  private String containerType;
  
  private String status;
  
  @JsonProperty("merchant_id")
  private Long merchantId;
  
  @JsonProperty("category_list")
  private Set<String> categoryList;
  
  @JsonProperty("packing_time")
  private String packingTime;
  
  @JsonProperty("packing_station")
  private Map<String, String> packingStation = new HashMap<>();
  
  @JsonProperty("booking_number")
  private String bookingNumber;
  
  private String laytime;
  
  @JsonProperty("cut_of_time")
  private String cutOfTime;
  
  private float payload;
  
  @JsonProperty("unit_of_measurement")
  private String unitOfMeasurement;
  
  @JsonProperty("port_of_loading")
  private String portOfLoading;
  
}
