package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
import com.crm.models.BiddingDocument;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.MerchantRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;

public class MerchantServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(MerchantServiceImplTest.class);

  @InjectMocks
  MerchantServiceImpl merchantServiceImpl;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private MerchantRepository merchantRepository;

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  PaginationRequest paginationRequest;

  Page<Merchant> pages;

  List<Merchant> merchants;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    merchants = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  @DisplayName("Create Merchant success")
  public void whenCreateMerchant_thenReturnMerchant() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(merchantRepository.save(Mockito.any(Merchant.class))).thenReturn(merchant);
    // then
    Merchant actualResult = merchantServiceImpl.createMerchant(request);
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Create Merchant when username exists")
  public void whenCreateMerchant_thenReturnDuplicateRecordException_username() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
      merchantServiceImpl.createMerchant(request);
    });
  }

  @Test
  @DisplayName("Create Merchant when Email exists")
  public void whenCreateMerchant_thenReturnDuplicateRecordException_Email() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
      merchantServiceImpl.createMerchant(request);
    });
  }

  @Test
  @DisplayName("Create Merchant when phone exists")
  public void whenCreateMerchant_thenReturnDuplicateRecordException_phone() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
      merchantServiceImpl.createMerchant(request);
    });
  }

  @Test
  @DisplayName("Create Merchant when companyCode exists")
  public void whenCreateMerchant_thenReturnDuplicateRecordException_companyCode() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.createMerchant(request);
    });
  }

  @Test
  @DisplayName("Create Merchant when tin exists")
  public void whenCreateMerchant_thenReturnDuplicateRecordException_Tin() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.createMerchant(request);
    });
  }

  @Test
  @DisplayName("Create Merchant when fax exist")
  public void whenCreateMerchant_thenReturnDuplicateRecordException_Fax() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.createMerchant(request);
    });
  }

  @Test
  @DisplayName("Create Merchant when Role NotFound")
  public void whenCreateMerchant_thenReturnNotFoundException_Role() {
    // given
    User user = new User();
    user.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);

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
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(false);
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      merchantServiceImpl.createMerchant(request);
    });
  }

  @Test
  @DisplayName("Get Merchant success")
  public void whenGetMerchant_thenMerchant() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(merchant));

    // then
    Merchant actualResult = merchantServiceImpl.getMerchant(merchant.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Get Merchant NotFound")
  public void whenGetMerchant_thenReturnNotFoundException_Merchant() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      merchantServiceImpl.getMerchant(merchant.getId());
    });
  }

  @Test
  @DisplayName("Get Merchants Success")
  public void whenGetMerchants_thenReturnMerchants() {
    // given
    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);

    merchants.add(merchant);
    pages = new PageImpl<>(merchants);

    // when
    when(merchantRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Merchant> actualPages = merchantServiceImpl.getMerchants(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumUserStatus.PENDING.name());
  }

  @Test
  @DisplayName("Edit Merchant Success")
  public void whenEditMerchant_thenReturnMerchant() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(merchant));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(false);
    when(merchantRepository.save(Mockito.any(Merchant.class))).thenReturn(merchant);

    // then
    Merchant actualResult = merchantServiceImpl.editMerchant(merchant.getId(), updates);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Edit Merchant when Merchant NotFound")
  public void whenEditMerchant_thenReturnNotFoundException_Merchant() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      merchantServiceImpl.editMerchant(merchant.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Merchant when Email Exist")
  public void whenEditMerchant_thenReturnDuplicateRecordException_Email() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(merchant));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.editMerchant(merchant.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Merchant when Phone Exist")
  public void whenEditMerchant_thenReturnDuplicateRecordException_Phone() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(merchant));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.editMerchant(merchant.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Merchant when CompanyCode Exist")
  public void whenEditMerchant_thenReturnDuplicateRecordException_CompanyCode() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(merchant));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(true);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.editMerchant(merchant.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Merchant when Tin Exist")
  public void whenEditMerchant_thenReturnDuplicateRecordException_Tin() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(merchant));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.editMerchant(merchant.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit Merchant when Fax Exist")
  public void whenEditMerchant_thenReturnDuplicateRecordException_Fax() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(merchant));
    when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    when(userRepository.existsByPhone(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByCompanyCode(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByTin(Mockito.anyString())).thenReturn(false);
    when(supplierRepository.existsByFax(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      merchantServiceImpl.editMerchant(merchant.getId(), updates);
    });
  }

  @Test
  @DisplayName("Remove Merchant Success")
  public void whenRemoveMerchant_thenReturnMerchant() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.existsById(Mockito.anyLong())).thenReturn(true);

    // then
    merchantServiceImpl.removeMerchant(merchant.getId());
  }

  @Test
  @DisplayName("Remove Merchant when Merchant NotFound")
  public void whenRemoveMerchant_thenReturnNotFoundException_Merchant() {
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

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    Collection<Outbound> outbounds = new ArrayList<>();
    outbounds.add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Collection<BiddingDocument> biddingDocuments = new ArrayList<>();
    biddingDocuments.add(biddingDocument);

    User user = new User();
    user.setId(1L);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("minhkq");
    merchant.setEmail("minhkq@gmail.com");
    merchant.setPhone("09123454");
    merchant.setAddress("Hanoi");
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite("facebook");
    merchant.setCompanyName("ALC");
    merchant.setCompanyCode("A012");
    merchant.setCompanyDescription("Very Good");
    merchant.setCompanyAddress("Ba Dinh");
    merchant.setFullname("Khong Quang Minh");
    merchant.setTin("1234567");
    merchant.setFax("02345678");
    merchant.setRatingValue(0D);
    merchant.setOutbounds(outbounds);
    merchant.setBiddingDocuments(biddingDocuments);

    // when
    when(merchantRepository.existsById(Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      merchantServiceImpl.removeMerchant(merchant.getId());
    });
  }
}
