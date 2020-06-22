package com.crm.payload.request;

import java.util.Set;

import com.crm.models.Address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsignmentRequest extends SupplyRequest{
 
  private String packingTime;
  
  private Address packingStation;
  
  private String bookingNumber;
  
  private String laytime;
  
  private String cutOffTime;
  
  private float payload;
  
  private String unitOfMeasurement;
  
  private Set<String> categories;
  
  private boolean fcl;
  
  private String portOfLoading; 
  
  
}
