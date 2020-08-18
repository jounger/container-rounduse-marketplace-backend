package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Driver;
import com.crm.models.dto.DriverDto;

public class DriverMapper {

  public static DriverDto toDriverDto(Driver driver) {
    if (driver == null) {
      return null;
    }

    DriverDto driverDto = new DriverDto();
    driverDto.setId(driver.getId());
    driverDto.setUsername(driver.getUsername());
    driverDto.setEmail(driver.getEmail());
    driverDto.setPhone(driver.getPhone());
    driverDto.setStatus(driver.getStatus());
    driverDto.setProfileImagePath(driver.getProfileImagePath());

    Set<String> roles = new HashSet<>();
    driver.getRoles().forEach(role -> roles.add(role.getName()));
    driverDto.setRoles(roles);
    driverDto.setAddress(driver.getAddress());
    driverDto.setFullname(driver.getFullname());
    driverDto.setDriverLicense(driver.getDriverLicense());
    if (driver.getLocation() != null) {
      driverDto.setLocation(GeolocationMapper.toGeolocationDto(driver.getLocation()));
    }

    return driverDto;
  }
}
