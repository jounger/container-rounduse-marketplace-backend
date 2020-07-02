package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Driver;
import com.crm.models.dto.DriverDto;
import com.crm.models.mapper.DriverMapper;
import com.crm.payload.request.DriverRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.DriverService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/driver")
public class DriverController {

  private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

  @Autowired
  private DriverService driverService;

  @GetMapping("/forwarder/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> getDriversByForwarder(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Driver> pages = driverService.getDriversByForwarder(id, request);
    PaginationResponse<DriverDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Driver> drivers = pages.getContent();
    List<DriverDto> driversDto = new ArrayList<>();
    drivers.forEach(driver -> driversDto.add(DriverMapper.toDriverDto(driver)));
    response.setContents(driversDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getDrivers(@Valid PaginationRequest request) {

    Page<Driver> pages = driverService.getDrivers(request);
    PaginationResponse<DriverDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Driver> drivers = pages.getContent();
    List<DriverDto> driversDto = new ArrayList<>();
    drivers.forEach(driver -> driversDto.add(DriverMapper.toDriverDto(driver)));
    response.setContents(driversDto);

    return ResponseEntity.ok(response);

  }

  @Transactional
  @PostMapping("/forwarder")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createDriver(@Valid @RequestBody DriverRequest request) {
    logger.info("Driver request: {}", request);
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long id = userDetails.getId();
    driverService.createDriver(id, request);
    return ResponseEntity.ok(new MessageResponse("Driver created successfully"));
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  public ResponseEntity<?> updateDriver(@Valid @RequestBody DriverRequest request) {
    Driver driver = driverService.updateDriver(request);
    DriverDto driverDto = DriverMapper.toDriverDto(driver);
    return ResponseEntity.ok(driverDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editDriver(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Driver driver = driverService.editDriver(id, updates);
    DriverDto driverDto = DriverMapper.toDriverDto(driver);
    return ResponseEntity.ok(driverDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeDriver(@PathVariable Long id) {
    driverService.removeDriver(id);
    return ResponseEntity.ok(new MessageResponse("Driver has remove successfully"));
  }
}
