package com.crm.services.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.NotFoundException;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Geolocation;
import com.crm.models.Role;
import com.crm.payload.request.DriverRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.DriverRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.DriverService;

@Service
public class DriverServiceImpl implements DriverService {

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ForwarderRepository forwarderRepository;

  @Override
  public Driver createDriver(String username, DriverRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException(ErrorMessage.USER_ALREADY_EXISTS);
    }
    Driver driver = new Driver();
    driver.setUsername(request.getUsername());

    String encoder = passwordEncoder.encode(request.getPassword());
    driver.setPassword(encoder);

    driver.setPhone(request.getPhone());
    driver.setEmail(request.getEmail());
    driver.setStatus(EnumUserStatus.ACTIVE.name());
    driver.setAddress(request.getAddress());

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName("ROLE_DRIVER")
        .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
    roles.add(userRole);
    driver.setRoles(roles);

    driver.setFullname(request.getFullname());
    driver.setDriverLicense(request.getDriverLicense());

    Forwarder forwarder = forwarderRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND));
    driver.setForwarder(forwarder);

    Geolocation location = new Geolocation();
    location.setLatitude(Constant.EMPTY_STRING);
    location.setLongitude(Constant.EMPTY_STRING);
    location.setDriver(driver);

    driver.setLocation(location);
    Driver _driver = driverRepository.save(driver);

    return _driver;
  }

  @Override
  public Driver getDriver(Long id) {
    Driver driver = driverRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_NOT_FOUND));
    return driver;
  }

  @Override
  public Page<Driver> getDrivers(PaginationRequest request) {
    Page<Driver> drivers = driverRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return drivers;
  }

  @Override
  public Page<Driver> getDriversByForwarder(String username, PaginationRequest request) {
    Page<Driver> drivers = driverRepository.findByForwarder(username,
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return drivers;
  }

  @Override
  public Driver editDriver(Long id, String username, Map<String, Object> updates) {
    if (forwarderRepository.existsByUsername(username)) {
      Driver driver = driverRepository.findById(id)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_NOT_FOUND));

      if (!driver.getForwarder().getUsername().equals(username)) {
        throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
      }

      String email = String.valueOf(updates.get("email"));
      if (updates.get("email") != null && !Tool.isEqual(driver.getEmail(), email)) {
        if (!userRepository.existsByEmail(email)) {
          driver.setEmail(email);
        } else {
          throw new DuplicateRecordException(ErrorMessage.USER_EMAIL_ALREADY_EXISTS);
        }
      }

      String phone = String.valueOf(updates.get("phone"));
      if (updates.get("phone") != null && !Tool.isEqual(driver.getPhone(), phone)) {
        if (!userRepository.existsByPhone(phone)) {
          driver.setPhone(phone);
        } else {
          throw new DuplicateRecordException(ErrorMessage.USER_PHONE_ALREADY_EXISTS);
        }
      }

      String address = String.valueOf(updates.get("address"));
      if (updates.get("address") != null && !Tool.isEqual(driver.getAddress(), address)) {
        driver.setAddress(address);
      }

      String status = String.valueOf(updates.get("status"));
      if (updates.get("status") != null && !Tool.isEqual(driver.getStatus(), status)) {
        EnumUserStatus eStatus = EnumUserStatus.findByName(status);
        driver.setStatus(eStatus.name());
      }

      String fullname = String.valueOf(updates.get("fullname"));
      if (updates.get("fullname") != null && !Tool.isEqual(driver.getFullname(), fullname)) {
        driver.setFullname(fullname);
      }

      String driverLicense = String.valueOf(updates.get("driverLicense"));
      if (updates.get("driverLicense") != null && !Tool.isEqual(driver.getDriverLicense(), driverLicense)) {
        driver.setDriverLicense(driverLicense);
      }

      Driver _driver = driverRepository.save(driver);

      return _driver;
    } else {
      throw new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND);
    }
  }

  @Override
  public void removeDriver(Long id, String username) {
    if (forwarderRepository.existsByUsername(username)) {

      Driver driver = driverRepository.findById(id)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_NOT_FOUND));
      driverRepository.delete(driver);
      if (!driver.getForwarder().getUsername().equals(username)) {
        throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
      }
    } else {
      throw new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND);
    }
  }

  @Override
  public Driver getDriverByUserName(String username) {
    Driver driver = driverRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_NOT_FOUND));
    return driver;
  }

}
