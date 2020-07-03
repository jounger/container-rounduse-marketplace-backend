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

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
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
  public Driver createDriver(Long id, DriverRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException("Error: User has been existed");
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
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    roles.add(userRole);
    driver.setRoles(roles);

    driver.setFullname(request.getFullname());
    driver.setDriverLicense(request.getDriverLicense());

    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Forwarder is not found"));
    driver.setForwarder(forwarder);

    driverRepository.save(driver);

    return driver;
  }

  @Override
  public Driver getDriver(Long id) {
    Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException("Driver is not found."));
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
  public Driver updateDriver(DriverRequest request) {
    Driver driver = driverRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Driver is not found."));

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
          .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
      roles.add(userRole);
    } else {
      rolesString.forEach(role -> {
        Role userRole = roleRepository.findByName(role)
            .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
        roles.add(userRole);
      });
    }
    driver.setRoles(roles);
    driver.setFullname(request.getFullname());
    driver.setDriverLicense(request.getDriverLicense());
    driverRepository.save(driver);
    return driver;
  }

  @Override
  public Driver editDriver(Long id, Map<String, Object> updates) {
    Driver driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException("Driver is not found."));

    /*
     * String password = (String) updates.get("password"); if (password != null) {
     * String encoder = passwordEncoder.encode(password);
     * driver.setPassword(encoder); }
     */

    String email = (String) updates.get("email");
    if (email != null && UserServiceImpl.isEmailChange(email, driver)) {
      driver.setEmail(email);
    }

    String phone = (String) updates.get("phone");
    if (phone != null && !phone.isEmpty()) {
      driver.setPhone(phone);
    }

    String address = (String) updates.get("address");
    if (address != null && !address.isEmpty()) {
      driver.setAddress(address);
    }

    String fullname = (String) updates.get("fullname");
    if (fullname != null && fullname.isEmpty()) {
      driver.setFullname(fullname);
    }

    String driverLicense = (String) updates.get("driverLicense");
    if (driverLicense != null && driverLicense.isEmpty()) {
      driver.setDriverLicense(driverLicense);
    }

    String location = (String) updates.get("location");
    if (location != null && !location.isEmpty()) {
      driver.setLocation(location);
    }

    driverRepository.save(driver);
    return driver;
  }

  @Override
  public void removeDriver(Long id) {
    if (driverRepository.existsById(id)) {
      driverRepository.deleteById(id);
    } else {
      throw new NotFoundException("Driver is not found.");
    }

  }

}
