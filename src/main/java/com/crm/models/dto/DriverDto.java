package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverDto extends UserDto {

  private String fullname;

  private String driverLicense;

  private GeolocationDto location;
}
