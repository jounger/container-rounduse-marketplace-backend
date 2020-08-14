package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
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

public class DriverServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(DriverServiceImplTest.class);

  @InjectMocks
  DriverServiceImpl driverServiceImpl;

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private ForwarderRepository forwarderRepository;

  PaginationRequest paginationRequest;

  Page<Driver> pages;

  List<Driver> drivers;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    drivers = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  @DisplayName("Create Driver success")
  public void whenCreateDriver_thenReturnDriver() {
    // given
    Role role = new Role();
    role.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(1L);

    DriverRequest driverRequest = new DriverRequest();
    driverRequest.setUsername("driver");
    driverRequest.setEmail("driver@gmail.com");
    driverRequest.setStatus("ACTIVE");
    driverRequest.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driverRequest.setPassword("123456");
    driverRequest.setPhone("0912324445");
    driverRequest.setFullname("Khong Quang Minh");
    driverRequest.setDriverLicense("023456");
    Set<String> roles = new HashSet<>();
    roles.add("ROLE_DRIVER");
    driverRequest.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(driverRepository.save(Mockito.any(Driver.class))).thenReturn(driver);
    // then
    Driver actualResult = driverServiceImpl.createDriver(forwarder.getUsername(), driverRequest);
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Create Driver when UserName Already Exist")
  public void whenCreateDriver_thenReturnDuplicateRecordException_UserName() {
    // given
    Role role = new Role();
    role.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(1L);

    DriverRequest driverRequest = new DriverRequest();
    driverRequest.setUsername("driver");
    driverRequest.setEmail("driver@gmail.com");
    driverRequest.setStatus("ACTIVE");
    driverRequest.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driverRequest.setPassword("123456");
    driverRequest.setPhone("0912324445");
    driverRequest.setFullname("Khong Quang Minh");
    driverRequest.setDriverLicense("023456");
    Set<String> roles = new HashSet<>();
    roles.add("ROLE_DRIVER");
    driverRequest.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      driverServiceImpl.createDriver(forwarder.getUsername(), driverRequest);
    });
  }

  @Test
  @DisplayName("Create Driver when Email Already Exist")
  public void whenCreateDriver_thenReturnDuplicateRecordException_Email() {
    // given
    Role role = new Role();
    role.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(1L);

    DriverRequest driverRequest = new DriverRequest();
    driverRequest.setUsername("driver");
    driverRequest.setEmail("driver@gmail.com");
    driverRequest.setStatus("ACTIVE");
    driverRequest.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driverRequest.setPassword("123456");
    driverRequest.setPhone("0912324445");
    driverRequest.setFullname("Khong Quang Minh");
    driverRequest.setDriverLicense("023456");
    Set<String> roles = new HashSet<>();
    roles.add("ROLE_DRIVER");
    driverRequest.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      driverServiceImpl.createDriver(forwarder.getUsername(), driverRequest);
    });
  }

  @Test
  @DisplayName("Create Driver when Phone Already Exist")
  public void whenCreateDriver_thenReturnDuplicateRecordException_Phone() {
    // given
    Role role = new Role();
    role.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(1L);

    DriverRequest driverRequest = new DriverRequest();
    driverRequest.setUsername("driver");
    driverRequest.setEmail("driver@gmail.com");
    driverRequest.setStatus("ACTIVE");
    driverRequest.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driverRequest.setPassword("123456");
    driverRequest.setPhone("0912324445");
    driverRequest.setFullname("Khong Quang Minh");
    driverRequest.setDriverLicense("023456");
    Set<String> roles = new HashSet<>();
    roles.add("ROLE_DRIVER");
    driverRequest.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      driverServiceImpl.createDriver(forwarder.getUsername(), driverRequest);
    });
  }

  @Test
  @DisplayName("Create Driver when Role NotFound")
  public void whenCreateDriver_thenReturnRoleNotFoundException() {
    // given
    Role role = new Role();
    role.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(1L);

    DriverRequest driverRequest = new DriverRequest();
    driverRequest.setUsername("driver");
    driverRequest.setEmail("driver@gmail.com");
    driverRequest.setStatus("ACTIVE");
    driverRequest.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driverRequest.setPassword("123456");
    driverRequest.setPhone("0912324445");
    driverRequest.setFullname("Khong Quang Minh");
    driverRequest.setDriverLicense("023456");
    Set<String> roles = new HashSet<>();
    roles.add("ROLE_DRIVER");
    driverRequest.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.createDriver(forwarder.getUsername(), driverRequest);
    });
  }

  @Test
  @DisplayName("Create Driver when Forwarder NotFound")
  public void whenCreateDriver_thenReturnForwarderNotFoundException() {
    // given
    Role role = new Role();
    role.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(1L);

    DriverRequest driverRequest = new DriverRequest();
    driverRequest.setUsername("driver");
    driverRequest.setEmail("driver@gmail.com");
    driverRequest.setStatus("ACTIVE");
    driverRequest.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driverRequest.setPassword("123456");
    driverRequest.setPhone("0912324445");
    driverRequest.setFullname("Khong Quang Minh");
    driverRequest.setDriverLicense("023456");
    Set<String> roles = new HashSet<>();
    roles.add("ROLE_DRIVER");
    driverRequest.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.createDriver(forwarder.getUsername(), driverRequest);
    });
  }

  @Test
  @DisplayName("Get Driver By Id success")
  public void whenGetDriver_thenReturnDriver() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));

    // then
    Driver actualResult = driverServiceImpl.getDriver(driver.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Get Driver By Id when Driver NotFound")
  public void whenGetDriver_thenReturnDriverNotFoundException() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.getDriver(driver.getId());
    });
  }

  @Test
  @DisplayName("Get Drivers success")
  public void whenGetDrivers_thenReturnDrivers() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    drivers.add(driver);
    pages = new PageImpl<Driver>(drivers);

    // when
    when(driverRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Driver> actualPages = driverServiceImpl.getDrivers(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Get DriversByForwarder success")
  public void whengetDriversByForwarder_thenReturnDrivers() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    drivers.add(driver);
    pages = new PageImpl<Driver>(drivers);

    // when
    when(driverRepository.findByForwarder(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Driver> actualPages = driverServiceImpl.getDriversByForwarder(forwarder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Get Driver By UserName success")
  public void whenGetDriverByUserName_thenReturnDriver() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));

    // then
    Driver actualResult = driverServiceImpl.getDriverByUserName(driver.getUsername());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Get Driver By Username when Driver NotFound")
  public void whenGetDriverByUsername_thenReturnDriverNotFoundException() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.getDriverByUserName(driver.getUsername());
    });
  }

  @Test
  @DisplayName("Remove Driver success")
  public void whenRemoveDriverSuccess() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));
    // then
    driverServiceImpl.removeDriver(driver.getId(), forwarder.getUsername());
  }

  @Test
  @DisplayName("Remove Driver when Forwarder NotFound")
  public void whenRemoveDriver_thenReturnForwarderNotFoundException() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.removeDriver(driver.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove Driver when Driver NotFound")
  public void whenRemoveDriver_thenReturnDriverNotFoundException() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.removeDriver(driver.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove Driver when Access Denied")
  public void whenRemoveDriver_thenAccessDeniedException() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      driverServiceImpl.removeDriver(driver.getId(), "XXXX");
    });
  }

  @Test
  @DisplayName("Edit Driver success")
  public void whenEditDriver_thenReturnDriver() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minh@gmail.com");
    updates.put("phone", "058281934");
    updates.put("address", "Ba Dinh, Ho Chi Minh, Vietnam");
    updates.put("status", "BANNED");
    updates.put("fullname", "Nguyen Huu Toan");
    updates.put("driverLicense", "0678523");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(driverRepository.save(Mockito.any(Driver.class))).thenReturn(driver);

    // then
    Driver actualResult = driverServiceImpl.editDriver(driver.getId(), forwarder.getUsername(), updates);
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Edit Driver when forwarder NotFound")
  public void whenEditDriver_thenReturnNotFoundException_forwarder() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minh@gmail.com");
    updates.put("phone", "058281934");
    updates.put("address", "Ba Dinh, Ho Chi Minh, Vietnam");
    updates.put("status", "BANNED");
    updates.put("fullname", "Nguyen Huu Toan");
    updates.put("driverLicense", "0678523");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.editDriver(driver.getId(), forwarder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit Driver when driver NotFound")
  public void whenEditDriver_thenReturnNotFoundException_Driver() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minh@gmail.com");
    updates.put("phone", "058281934");
    updates.put("address", "Ba Dinh, Ho Chi Minh, Vietnam");
    updates.put("status", "BANNED");
    updates.put("fullname", "Nguyen Huu Toan");
    updates.put("driverLicense", "0678523");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      driverServiceImpl.editDriver(driver.getId(), forwarder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit Driver when Access Denied")
  public void whenEditDriver_thenReturnAccessDeniedException() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minh@gmail.com");
    updates.put("phone", "058281934");
    updates.put("address", "Ba Dinh, Ho Chi Minh, Vietnam");
    updates.put("status", "BANNED");
    updates.put("fullname", "Nguyen Huu Toan");
    updates.put("driverLicense", "0678523");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      driverServiceImpl.editDriver(driver.getId(), "XXXX", updates);
    });
  }

  @Test
  @DisplayName("Edit Driver when Email Exists")
  public void whenEditDriver_thenReturnDuplicateRecordException_Email() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minh@gmail.com");
    updates.put("phone", "058281934");
    updates.put("address", "Ba Dinh, Ho Chi Minh, Vietnam");
    updates.put("status", "BANNED");
    updates.put("fullname", "Nguyen Huu Toan");
    updates.put("driverLicense", "0678523");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      driverServiceImpl.editDriver(driver.getId(), forwarder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit Driver when Phone Exists")
  public void whenEditDriver_thenReturnDuplicateRecordException_Phone() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minh@gmail.com");
    updates.put("phone", "058281934");
    updates.put("address", "Ba Dinh, Ho Chi Minh, Vietnam");
    updates.put("status", "BANNED");
    updates.put("fullname", "Nguyen Huu Toan");
    updates.put("driverLicense", "0678523");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");
    driver.setEmail("driver@gmail.com");
    driver.setStatus("ACTIVE");
    driver.setAddress("Ba Dinh, Ha Noi, Vietnam");
    driver.setPassword("123456");
    driver.setPhone("0912324445");
    driver.setFullname("Khong Quang Minh");
    driver.setDriverLicense("023456");
    Set<Role> roles = new HashSet<>();
    roles.add(role);
    driver.setRoles(roles);
    driver.setForwarder(forwarder);

    // when
    when(forwarderRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(driverRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(driver));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      driverServiceImpl.editDriver(driver.getId(), forwarder.getUsername(), updates);
    });
  }
}
