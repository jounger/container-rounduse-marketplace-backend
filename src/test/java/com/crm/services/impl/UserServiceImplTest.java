package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import com.crm.exception.NotFoundException;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;

public class UserServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

  @InjectMocks
  UserServiceImpl userServiceImpl;

  @Mock
  UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  PaginationRequest paginationRequest;

  List<User> users = new ArrayList<>();

  Page<User> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  public void whenCreateUser_thenReturnUser() {
    // given
    SignUpRequest request = new SignUpRequest();
    request.setId(1L);
    request.setUsername("nguyenvanan");
    request.setPassword("123456");
    request.setPhone("0967390098");
    request.setEmail("annvse@fpt.edu.vn");
    request.setAddress("HN, Vietnam");
    Set<String> roles = new HashSet<String>();
    roles.add("FORWARDER");
    request.setRoles(roles);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    // when
    when(passwordEncoder.encode(Mockito.anyString())).thenReturn("random-hash");
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(role));
    when(userRepository.save(Mockito.any(User.class))).thenReturn(null);
    // then
    userServiceImpl.createUser(request);
  }

  @Test
  public void whenCreateUser_thenReturn500() {
    // given
    SignUpRequest request = new SignUpRequest();
    request.setId(1L);
    request.setUsername("nguyenvanan");
    request.setPassword("123456");
    request.setPhone("0967390098");
    request.setEmail("annvse@fpt.edu.vn");
    request.setAddress("HN, Vietnam");
    Set<String> roles = new HashSet<String>();
    roles.add("FORWARDER");
    request.setRoles(roles);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    // when
    when(passwordEncoder.encode(Mockito.anyString())).thenReturn("random-hash");
    when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(userRepository.save(Mockito.any(User.class))).thenReturn(null);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      userServiceImpl.createUser(request);
    });
  }

  @Test
  public void whenCreateUser_thenReturn404() {
    // given
    SignUpRequest request = new SignUpRequest();
    request.setId(1L);
    request.setUsername("nguyenvanan");
    request.setPassword("123456");
    request.setPhone("0967390098");
    request.setEmail("annvse@fpt.edu.vn");
    request.setAddress("HN, Vietnam");
    Set<String> roles = new HashSet<String>();
    roles.add("FORWARDER");
    request.setRoles(roles);

    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    // when
    when(passwordEncoder.encode(Mockito.anyString())).thenReturn("random-hash");
    when(roleRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
    when(userRepository.save(Mockito.any(User.class))).thenReturn(null);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      userServiceImpl.createUser(request);
    });
  }

  @Test
  public void whenGetUsers_thenReturnUsers() {
    // given
    User user1 = new User();
    user1.setId(1L);
    user1.setUsername("nguyenvanan");
    user1.setPassword("123456");
    user1.setPhone("0967390098");
    user1.setEmail("annvse@fpt.edu.vn");
    user1.setAddress("HN, Vietnam");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);
    user1.setRoles(roles);
    users.add(user1);
    pages = new PageImpl<User>(users);
    // when
    when(userRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);
    // then
    Page<User> actualPages = userServiceImpl.getUsers(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getUsername()).isEqualTo("nguyenvanan");
  }

  @Test
  public void whenChangeStatus_thenReturnUser() {
    // given
    User user1 = new User();
    user1.setId(1L);
    user1.setUsername("nguyenvanan");
    user1.setStatus("PENDING");

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "ACTIVE");

    // when
    when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));
    user1.setStatus("ACTIVE");
    when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

    // then
    User actualResult = userServiceImpl.changeStatus(user1.getId(), updates);
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getStatus()).isEqualTo(updates.get("status"));
  }
}
