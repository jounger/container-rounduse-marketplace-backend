package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
  
  private Long id;
  
  @NotBlank
  private String name;
  
}
