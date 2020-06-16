package com.crm.payload.request;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import com.crm.models.Address;
import com.crm.models.Category;
import com.crm.models.Port;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsignmentRequest {
  
  private String shippingLineName;
  
  private String containerType;
  
  private String status;
  
  private Date packingTime;
  
  private Address packingStation;
  
  private String PIC;
  
  private String bookingNumber;
  
  private Date laytime;
  
  private Date cutOftime;
  
  private float payload;
  
  private String unitOfMeasurement;
  
  private Set<Category> categories = new HashSet<Category>();
  
  private boolean FCL;
  
  private Port portOfLoading; 
  
  
}
