package com.crm.payload.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoleRequest {
  
  private Long id;
  
  @NotBlank
  @Size(min=5, max = 20)
  private String name;
  
  private List<String> permissions;
  
}
