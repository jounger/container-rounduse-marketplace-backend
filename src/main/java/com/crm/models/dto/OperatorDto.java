package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorDto extends UserDto {
  
  private String fullname;

  private boolean isRoot;
}
