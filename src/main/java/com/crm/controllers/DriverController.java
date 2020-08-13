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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Driver;
import com.crm.models.dto.DriverDto;
import com.crm.models.mapper.DriverMapper;
import com.crm.payload.request.DriverRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.DriverService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/driver")
public class DriverController {

  private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

  @Autowired
  private DriverService driverService;

  @GetMapping("/forwarder")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> getDriversByForwarder(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Driver> pages = driverService.getDriversByForwarder(username, request);
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

  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getDriverById(@PathVariable Long id) {
    Driver driver = driverService.getDriver(id);
    DriverDto driverDto = DriverMapper.toDriverDto(driver);
    return ResponseEntity.ok(driverDto);
  }

  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  @RequestMapping(method = RequestMethod.GET, params = { "username" })
  public ResponseEntity<?> getDriver(@RequestParam String username) {
    Driver driver = driverService.getDriverByUserName(username);
    DriverDto driverDto = DriverMapper.toDriverDto(driver);
    return ResponseEntity.ok(driverDto);
  }

  @GetMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Driver driver = driverService.createDriver(username, request);
    DriverDto driverDto = DriverMapper.toDriverDto(driver);

    // Set default response body
    DefaultResponse<DriverDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_DRIVER_SUCCESSFULLY);
    defaultResponse.setData(driverDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editDriver(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Driver driver = driverService.editDriver(id, username, updates);
    DriverDto driverDto = DriverMapper.toDriverDto(driver);

    // Set default response body
    DefaultResponse<DriverDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_DRIVER_SUCCESSFULLY);
    defaultResponse.setData(driverDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeDriver(@PathVariable Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    driverService.removeDriver(id, username);

    // Set default response body
    DefaultResponse<DriverDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_DRIVER_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
