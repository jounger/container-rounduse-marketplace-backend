package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequest {

  private Long id;
  
  @NotBlank
  @Size(min=5, max = 20)
  private String name;

  @NotBlank
  @Size(min=5, max = 100)
  private String description;
}
