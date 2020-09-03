package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Booking;
import com.crm.models.ContainerType;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.Role;
import com.crm.models.ShippingLine;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;

public class ForwarderServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(ForwarderServiceImplTest.class);

  @InjectMocks
  ForwarderServiceImpl forwarderServiceImpl;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private ForwarderRepository forwarderRepository;

  @Mock
  private OutboundRepository outboundRepository;

  PaginationRequest paginationRequest;

  Page<Forwarder> pages;

  List<Forwarder> forwarders;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    forwarders = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  @DisplayName("Create Forwarder success")
  public void whenCreateForwarder_thenReturnForwarder() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(false);
    when(forwarderRepository.save(Mockito.any(Forwarder.class))).thenReturn(forwarder);
    // then
    Forwarder actualResult = forwarderServiceImpl.createForwarder(request);
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Create Forwarder when username exists")
  public void whenCreateForwarder_thenReturnDuplicateRecordException_username() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.createForwarder(request);
    });
  }

  @Test
  @DisplayName("Create Forwarder when email exists")
  public void whenCreateForwarder_thenReturnDuplicateRecordException_email() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.createForwarder(request);
    });
  }

  @Test
  @DisplayName("Create Forwarder when Phone exists")
  public void whenCreateForwarder_thenReturnDuplicateRecordException_Phone() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.createForwarder(request);
    });
  }

  @Test
  @DisplayName("Create Forwarder when Role Not found")
  public void whenCreateForwarder_thenReturn404_Role() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      forwarderServiceImpl.createForwarder(request);
    });
  }

  @Test
  @DisplayName("Create Forwarder when CompanyCode exists")
  public void whenCreateForwarder_thenReturnDuplicateRecordException_CompanyCode() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.createForwarder(request);
    });
  }

  @Test
  @DisplayName("Create Forwarder when Tin exists")
  public void whenCreateForwarder_thenReturnDuplicateRecordException_Tin() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.createForwarder(request);
    });
  }

  @Test
  @DisplayName("Create Forwarder when Fax exists")
  public void whenCreateForwarder_thenReturnDuplicateRecordException_Fax() {
    // given
    User user = new User();
    user.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);

    Role role = new Role();
    role.setId(1L);

    SupplierRequest request = new SupplierRequest();
    request.setUsername("minhkq");
    request.setEmail("minhkq@gmail.com");
    request.setPhone("09123454");
    request.setAddress("Hanoi");
    request.setStatus(EnumUserStatus.PENDING.name());
    request.setWebsite("facebook");
    request.setCompanyName("ALC");
    request.setCompanyCode("A012");
    request.setCompanyDescription("Very Good");
    request.setCompanyAddress("Ba Dinh");
    request.setFullname("Khong Quang Minh");
    request.setTin("1234567");
    request.setFax("02345678");
    request.setRatingValue(0);

    // when
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.createForwarder(request);
    });
  }

  @Test
  @DisplayName("Get Forwarder success")
  public void whenGetForwarder_thenReturnForwarder() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(forwarder));

    // then
    Forwarder actualResult = forwarderServiceImpl.getForwarder(forwarder.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Get Forwarder NotFound")
  public void whenGetForwarder_thenReturnNotFoundException_Forwarder() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      forwarderServiceImpl.getForwarder(forwarder.getId());
    });
  }

  @Test
  @DisplayName("Get Forwarders Success")
  public void whenGetForwarders_thenReturnForwarders() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);

    forwarders.add(forwarder);
    pages = new PageImpl<>(forwarders);

    // when
    when(forwarderRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Forwarder> actualPages = forwarderServiceImpl.getForwarders(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumUserStatus.PENDING.name());
  }

  @Test
  @DisplayName("Edit Forwarder Success")
  public void whenEditForwarder_thenReturnForwarder() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");
    updates.put("status", EnumUserStatus.ACTIVE.name());

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(forwarder));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(false);
    when(forwarderRepository.save(Mockito.any(Forwarder.class))).thenReturn(forwarder);

    // then
    Forwarder actualResult = forwarderServiceImpl.editForwarder(forwarder.getId(), updates);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Edit Forwarder when Forwarder NotFound")
  public void whenEditForwarder_thenReturnNotFoundException_Forwarder() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      forwarderServiceImpl.editForwarder(forwarder.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Forwarder when Email Exist")
  public void whenEditForwarder_thenReturnDuplicateRecordException_Email() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(forwarder));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.editForwarder(forwarder.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Forwarder when Phone Exist")
  public void whenEditForwarder_thenReturnDuplicateRecordException_Phone() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(forwarder));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.editForwarder(forwarder.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Forwarder when CompanyCode Exist")
  public void whenEditForwarder_thenReturnDuplicateRecordException_CompanyCode() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(forwarder));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(true);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.editForwarder(forwarder.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Forwarder when Tin Exist")
  public void whenEditForwarder_thenReturnDuplicateRecordException_Tin() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(forwarder));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.editForwarder(forwarder.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Forwarder when Fax Exist")
  public void whenEditForwarder_thenReturnDuplicateRecordException_Fax() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(forwarder));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      forwarderServiceImpl.editForwarder(forwarder.getId(), updates);
    });
  }

  @Test
  @DisplayName("Remove Forwarder Success")
  public void whenRemoveForwarder_thenReturnForwarder() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.existsById(Mockito.anyLong())).thenReturn(true);

    // then
    forwarderServiceImpl.removeForwarder(forwarder.getId());
  }

  @Test
  @DisplayName("Remove Forwarder when Forwarder NotFound")
  public void whenRemoveForwarder_thenReturnNotFoundException_Forwarder() {
    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("email", "minhT@gmail.com");
    updates.put("phone", "012323456");
    updates.put("address", "Ho Chi Minh");
    updates.put("website", "FFFFF");
    updates.put("fullname", "Nguyen Quang Huy");
    updates.put("companyName", "JPD");
    updates.put("companyCode", "J0001");
    updates.put("companyDescription", "Not Good");
    updates.put("tin", "0123456");
    updates.put("fax", "0143456");

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Collection<Inbound> inbounds = new ArrayList<>();
    inbounds.add(inbound);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);
    forwarder.setInbounds(inbounds);

    // when
    when(forwarderRepository.existsById(Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      forwarderServiceImpl.removeForwarder(forwarder.getId());
    });
  }

  @Test
  @DisplayName("Find Forwarders By Outbound Success")
  public void whenFindForwardersByOutbound_thenReturnForwarders() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);

    forwarders.add(forwarder);
    pages = new PageImpl<>(forwarders);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(forwarderRepository.findByOutbound(Mockito.anyString(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyList(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Forwarder> actualPages = forwarderServiceImpl.findForwardersByOutbound(outbound.getId(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumUserStatus.PENDING.name());
  }

  @Test
  @DisplayName("Find Forwarders By Outbound when outbound notfound")
  public void whenFindForwardersByOutbound_thenReturn404_Outbound() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("minhkq");
    forwarder.setEmail("minhkq@gmail.com");
    forwarder.setPhone("09123454");
    forwarder.setAddress("Hanoi");
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite("facebook");
    forwarder.setCompanyName("ALC");
    forwarder.setCompanyCode("A012");
    forwarder.setCompanyDescription("Very Good");
    forwarder.setCompanyAddress("Ba Dinh");
    forwarder.setFullname("Khong Quang Minh");
    forwarder.setTin("1234567");
    forwarder.setFax("02345678");
    forwarder.setRatingValue(0D);

    forwarders.add(forwarder);
    pages = new PageImpl<>(forwarders);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    when(forwarderRepository.findByOutbound(Mockito.anyString(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyList(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      forwarderServiceImpl.findForwardersByOutbound(outbound.getId(), paginationRequest);
    });
  }
}
