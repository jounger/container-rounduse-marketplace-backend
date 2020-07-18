package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Operator;
import com.crm.models.Role;
import com.crm.payload.request.OperatorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.OperatorRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.OperatorService;

@Service
public class OperatorServiceImpl implements OperatorService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private OperatorRepository operatorRepository;

  @Override
  public Operator createOperator(OperatorRequest request) {

    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException("Error: User has been existed");
    }
    Operator operator = new Operator();
    operator.setUsername(request.getUsername());

    String encoder = passwordEncoder.encode(request.getPassword());
    operator.setPassword(encoder);

    String role = request.getRoles().iterator().next();
    if (role.equalsIgnoreCase("ROLE_MODERATOR")) {
      Role userRole = roleRepository.findByName("ROLE_MODERATOR")
          .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
      operator.getRoles().add(userRole);
    } else if (role.equalsIgnoreCase("ROLE_ADMIN")) {
      Role userRole = roleRepository.findByName("ROLE_ADMIN")
          .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
      operator.getRoles().add(userRole);
    } else {
      throw new NotFoundException("Error: Role is not found");
    }

    operator.setPhone(request.getPhone());

    if (UserServiceImpl.isEmailChange(request.getEmail(), operator)) {
      operator.setEmail(request.getEmail());
    }

    operator.setAddress(request.getAddress());
    operator.setStatus(EnumUserStatus.ACTIVE.name());
    operator.setFullname(request.getFullname());
    operator.setIsRoot(false);

    operatorRepository.save(operator);

    return operator;
  }

  @Override
  public Page<Operator> getOperators(PaginationRequest request) {
    Page<Operator> pages = operatorRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return pages;
  }

  @Override
  public Operator getOperatorById(Long id) {
    Operator operator = operatorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Operator is not found."));
    return operator;
  }

  @Override
  public Operator updateOperator(OperatorRequest request) {
    Operator operator = operatorRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Error: Operator is not found"));

    operator.setUsername(request.getUsername());

    /*
     * String encoder = passwordEncoder.encode(request.getPassword());
     * operator.setPassword(encoder);
     */

    Role userRole = roleRepository.findByName("ROLE_MODERATOR")
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    operator.getRoles().add(userRole);

    operator.setPhone(request.getPhone());
    operator.setEmail(request.getEmail());
    operator.setAddress(request.getAddress());

    EnumUserStatus status = EnumUserStatus.findByName(request.getStatus());
    if (status != null) {
      operator.setStatus(EnumUserStatus.ACTIVE.name());
    } else {
      throw new NotFoundException("Status is not found.");
    }

    operator.setFullname(request.getFullname());
    operator.setIsRoot(request.getIsRoot());
    operatorRepository.save(operator);

    return operator;
  }

  @Override
  public Operator editOperator(Long id, Map<String, Object> updates) {
    Operator operator = operatorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Operator is not found."));

    /*
     * String password = String.valueOf( updates.get("password")); if (password !=
     * null && !password.isEmpty()) { String encoder =
     * passwordEncoder.encode(password); operator.setPassword(encoder); }
     */

    String email = String.valueOf(updates.get("email"));
    if (updates.get("email") != null && !Tool.isEqual(operator.getEmail(), email)) {
      if (!userRepository.existsByEmail(email)) {
        operator.setEmail(email);
      } else {
        throw new DuplicateRecordException("Email has been existed.");
      }
    }

    String phone = String.valueOf(updates.get("phone"));
    if (updates.get("phone") != null && !Tool.isEqual(operator.getPhone(), phone)) {
      if (!userRepository.existsByPhone(phone)) {
        operator.setPhone(phone);
      } else {
        throw new DuplicateRecordException("Phone number has been existed.");
      }
    }

    String address = String.valueOf(updates.get("address"));
    if (updates.get("address") != null && !Tool.isEqual(operator.getAddress(), address)) {
      operator.setAddress(address);
    }

    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(operator.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status);
      operator.setStatus(eStatus.name());
    } else {
      throw new NotFoundException("Status is not found.");
    }

    String fullname = String.valueOf(updates.get("fullname"));
    if (updates.get("fullname") != null && !Tool.isEqual(operator.getFullname(), fullname)) {
      operator.setFullname(fullname);
    }

    operatorRepository.save(operator);
    return operator;
  }

  @Override
  public void removeOperator(Long id) {

    if (operatorRepository.existsById(id)) {
      operatorRepository.deleteById(id);
    } else {
      throw new NotFoundException("ERROR: Operator is not found.");
    }
  }

}
