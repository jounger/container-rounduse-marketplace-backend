package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Driver;
import com.crm.payload.request.DriverRequest;
import com.crm.payload.request.PaginationRequest;

public interface DriverService {

  Driver createDriver(String username, DriverRequest request);

  Driver getDriver(Long id);

  Driver getDriverByUsername(String username);

  Page<Driver> getDrivers(PaginationRequest request);

  Page<Driver> getDriversByForwarder(String username, PaginationRequest request);

  Driver editDriver(Long id, String username, Map<String, Object> updates);

  void removeDriver(Long id, String username);
}
