package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequest extends SignUpRequest {

  @NotBlank
  private String fullname;

  @NotBlank
  private String driverLicense;

}
