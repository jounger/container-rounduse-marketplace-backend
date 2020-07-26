package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Driver;
import com.crm.payload.request.DriverRequest;
import com.crm.payload.request.PaginationRequest;

public interface DriverService {

  Driver createDriver(Long userId, DriverRequest request);

  Driver getDriver(Long id);

  Driver getDriverByUserName(String username);

  Page<Driver> getDrivers(PaginationRequest request);

  Page<Driver> getDriversByForwarder(Long id, PaginationRequest request);

  Driver updateDriver(Long userId, DriverRequest request);

  Driver editDriver(Long id, Long userId, Map<String, Object> updates);

  void removeDriver(Long id, Long userId);
}
