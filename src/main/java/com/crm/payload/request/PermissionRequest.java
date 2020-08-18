package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PermissionRequest {

  private Long id;
  
  @NotBlank
  @Size(min=3, max = 20)
  private String name;

  @NotBlank
  @Size(min=5, max = 100)
  private String description;
}
