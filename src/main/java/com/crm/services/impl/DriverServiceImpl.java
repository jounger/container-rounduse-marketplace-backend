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
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
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
  public Driver createDriver(Long userId, DriverRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException(ErrorConstant.USER_ALREADY_EXISTS);
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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.ROLE_NOT_FOUND));
    roles.add(userRole);
    driver.setRoles(roles);

    driver.setFullname(request.getFullname());
    driver.setDriverLicense(request.getDriverLicense());

    Forwarder forwarder = forwarderRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND));
    driver.setForwarder(forwarder);

    Geolocation location = new Geolocation();
    location.setLatitude(Constant.EMPTY_STRING);
    location.setLongitude(Constant.EMPTY_STRING);
    location.setDriver(driver);

    driver.setLocation(location);
    driverRepository.save(driver);

    return driver;
  }

  @Override
  public Driver getDriver(Long id) {
    Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.DRIVER_NOT_FOUND));
    return driver;
  }

  @Override
  public Page<Driver> getDrivers(PaginationRequest request) {
    Page<Driver> drivers = driverRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return drivers;
  }

  @Override
  public Page<Driver> getDriversByForwarder(Long id, PaginationRequest request) {
    Page<Driver> drivers = driverRepository.findByForwarder(id,
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return drivers;
  }

  @Override
  public Driver updateDriver(Long userId, DriverRequest request) {
    if (forwarderRepository.existsById(userId)) {
      Driver driver = driverRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException(ErrorConstant.DRIVER_NOT_FOUND));

      if (!driver.getForwarder().getId().equals(userId)) {
        throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
      }

      /*
       * String encoder = passwordEncoder.encode(request.getPassword());
       * driver.setPassword(encoder);
       */

      driver.setPhone(request.getPhone());

      if (UserServiceImpl.isEmailChange(request.getEmail(), driver)) {
        driver.setEmail(request.getEmail());
      }

      EnumUserStatus status = EnumUserStatus.findByName(request.getStatus());
      if (status != null) {
        driver.setStatus(status.name());
      }

      Set<String> rolesString = request.getRoles();
      Set<Role> roles = new HashSet<Role>();
      if (rolesString == null) {
        Role userRole = roleRepository.findByName("ROLE_DRIVER")
            .orElseThrow(() -> new NotFoundException(ErrorConstant.ROLE_NOT_FOUND));
        roles.add(userRole);
      } else {
        rolesString.forEach(role -> {
          Role userRole = roleRepository.findByName(role)
              .orElseThrow(() -> new NotFoundException(ErrorConstant.ROLE_NOT_FOUND));
          roles.add(userRole);
        });
      }
      driver.setRoles(roles);
      driver.setFullname(request.getFullname());
      driver.setDriverLicense(request.getDriverLicense());
      driverRepository.save(driver);
      return driver;
    } else {
      throw new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND);
    }
  }

  @Override
  public Driver editDriver(Long id, Long userId, Map<String, Object> updates) {
    if (forwarderRepository.existsById(userId)) {
      Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.DRIVER_NOT_FOUND));

      if (!driver.getForwarder().getId().equals(userId)) {
        throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
      }

      /*
       * String password = String.valueOf( updates.get("password")); if (password !=
       * null) { String encoder = passwordEncoder.encode(password);
       * driver.setPassword(encoder); }
       */

      String email = String.valueOf(updates.get("email"));
      if (updates.get("email") != null && !Tool.isEqual(driver.getEmail(), email)) {
        if (!userRepository.existsByEmail(email)) {
          driver.setEmail(email);
        } else {
          throw new DuplicateRecordException(ErrorConstant.USER_EMAIL_ALREADY_EXISTS);
        }
      }

      String phone = String.valueOf(updates.get("phone"));
      if (updates.get("phone") != null && !Tool.isEqual(driver.getPhone(), phone)) {
        if (!userRepository.existsByPhone(phone)) {
          driver.setPhone(phone);
        } else {
          throw new DuplicateRecordException(ErrorConstant.USER_PHONE_ALREADY_EXISTS);
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

      driverRepository.save(driver);
      return driver;
    } else {
      throw new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND);
    }
  }

  @Override
  public void removeDriver(Long id, Long userId) {
    if (forwarderRepository.existsById(userId)) {

      Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.DRIVER_NOT_FOUND));

      if (!driver.getForwarder().getId().equals(userId)) {
        throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
      }
    } else {
      throw new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND);
    }
  }

}
