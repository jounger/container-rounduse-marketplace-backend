package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DriverRequest extends SignUpRequest {

  @NotBlank
  private String driverLicense;

  private GeolocationRequest location;

}
