package com.crm.models.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForwarderDto extends SupplierDto{
  
  Double ratingValue;
  
  List<String> drivers;
}
