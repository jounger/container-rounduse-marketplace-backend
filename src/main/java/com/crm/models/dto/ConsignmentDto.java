package com.crm.models.dto;

import java.util.Set;

import com.crm.models.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsignmentDto {
  private String shippingLine;
  private String containerType;
  private String status;
  private Long merchantId;
  private Set<String> categoryList;
  private String packingTime;
  private Address packingStation;
  private String bookingNumber;
  private String laytime;
  private String cutOfTime;
  private float payload;
  private float unitOfMeasurement;
  private String portOfLoading;
}
