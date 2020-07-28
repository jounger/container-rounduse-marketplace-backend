package com.crm.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorConstant;
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
import com.crm.specification.builder.UserSpecificationsBuilder;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void createUser(SignUpRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException(ErrorConstant.USER_ALREADY_EXISTS);
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
          .orElseThrow(() -> new NotFoundException(ErrorConstant.ROLE_NOT_FOUND));
      roles.add(userRole);
    } else {
      rolesString.forEach(role -> {
        for (int i = 0; i < rolesString.size(); i++) {
          Role userRole = roleRepository.findByName(role)
              .orElseThrow(() -> new NotFoundException(ErrorConstant.ROLE_NOT_FOUND));
          roles.add(userRole);
        }
      });
    }
    user.setRoles(roles);
    String address = request.getAddress();
    if (address == null) {
      throw new NotFoundException(ErrorConstant.USER_ADDRESS_NOT_FOUND);
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
      pages = userRepository
          .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else {
      pages = userRepository.findByStatus(EnumUserStatus.findByName(request.getStatus()),
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return pages;
  }

  @Override
  public Page<User> searchUsers(PaginationRequest request, String search) {
    // Extract data from search string
    UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<User> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<User> pages = userRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public User changeStatus(Long id, Map<String, Object> updates) {
    String status = String.valueOf(updates.get("status"));
    EnumUserStatus eStatus = EnumUserStatus.findByName(status);
    if (status != null && eStatus != null) {
      User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.USER_NOT_FOUND));
      user.setStatus(eStatus.name());
      userRepository.save(user);
      return user;
    } else {
      throw new NotFoundException("Status is not found.");
    }
  }

  @Override
  public List<User> getUsersByRole(String roleName) {
    List<User> users = userRepository.findByRole(roleName);
    if (users == null) {
      throw new NotFoundException("Error: User is not found");
    }
    return users;
  }
}
