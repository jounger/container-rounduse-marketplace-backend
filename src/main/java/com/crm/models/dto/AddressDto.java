package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
  
  private Long id;
  
  private String street;
  
  private String county;
  
  private String city;
  
  private String country;
  
  private String postalCode;
}
