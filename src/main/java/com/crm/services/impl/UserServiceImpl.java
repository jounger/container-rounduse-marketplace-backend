package com.crm.services.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void saveUser(SignUpRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException("Error: User has been existed");
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPhone(request.getPhone());
    user.setStatus(EnumUserStatus.PENDING.name());
    Set<String> rolesString = request.getRoles();
    Set<Role> roles = new HashSet<>();

    if (rolesString == null) {
      Role userRole = roleRepository.findByName("ROLE_OTHER")
          .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
      roles.add(userRole);
    } else {
      rolesString.forEach(role -> {
        for (int i = 0; i < rolesString.size(); i++) {
          Role userRole = roleRepository.findByName(role)
              .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
          roles.add(userRole);
        }
      });
    }
    user.setRoles(roles);
    String address = request.getAddress();
    if (address == null) {
      throw new NotFoundException("Error: Address is not found");
    } else {
      user.setAddress(address);
    }
    String encoder = passwordEncoder.encode(request.getPassword());
    user.setPassword(encoder);
    userRepository.save(user);
  }

  @Override
  public Page<User> getUsers(PaginationRequest request) {
    Page<User> pages = null;
    if (request.getStatus() == null) {
      pages = userRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    } else {
      pages = userRepository.findByStatus(EnumUserStatus.findByName(request.getStatus()),
          PageRequest.of(request.getPage(), request.getLimit()));
    }
    return pages;
  }

  @Override
  public User changeStatus(Long id, Map<String, Object> updates) {
    String status = (String) updates.get("status");
    EnumUserStatus eStatus = EnumUserStatus.findByName(status);
    if (status != null && eStatus != null) {
      User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Error: User is not found"));
      user.setStatus(eStatus.name());
      userRepository.save(user);
      return user;
    } else {
      throw new NotFoundException("Status is not found.");
    }
  }

  public static boolean isEmailChange(String email, User user) {
    if (email.equalsIgnoreCase(user.getEmail())) {
      return false;
    }
    return true;
  }
}
