package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorRequest extends SignUpRequest {
  
  @NotBlank
  private String fullname;

  private Boolean isRoot;
}
