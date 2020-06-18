package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequest {

  private Long id;
  
  private String name;

  private String description;
}
