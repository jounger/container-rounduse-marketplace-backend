package com.crm.payload.request;

import java.util.Set;

import com.crm.models.Address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsignmentRequest {
  
  private String shippingLineName;
  
  private String containerType;
  
  private String status;
  
  private String packingTime;
  
  private Address packingStation;
  
  private String PIC;
  
  private String bookingNumber;
  
  private String laytime;
  
  private String cutOftime;
  
  private float payload;
  
  private float unitOfMeasurement;
  
  private Set<String> categories;
  
  private String portOfLoading; 
  
  
}
