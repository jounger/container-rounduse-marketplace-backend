package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorConstant;
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
      throw new DuplicateRecordException(ErrorConstant.USER_ALREADY_EXISTS);
    }
    Operator operator = new Operator();
    operator.setUsername(request.getUsername());

    String encoder = passwordEncoder.encode(request.getPassword());
    operator.setPassword(encoder);

    String role = request.getRoles().iterator().next();
    if (role.equalsIgnoreCase("ROLE_MODERATOR")) {
      Role userRole = roleRepository.findByName("ROLE_MODERATOR")
          .orElseThrow(() -> new NotFoundException(ErrorConstant.ROLE_NOT_FOUND));
      operator.getRoles().add(userRole);
    } else if (role.equalsIgnoreCase("ROLE_ADMIN")) {
      Role userRole = roleRepository.findByName("ROLE_ADMIN")
          .orElseThrow(() -> new NotFoundException(ErrorConstant.ROLE_NOT_FOUND));
      operator.getRoles().add(userRole);
    } else {
      throw new NotFoundException(ErrorConstant.ROLE_NOT_FOUND);
    }

    operator.setPhone(request.getPhone());
    operator.setEmail(request.getEmail());
    operator.setAddress(request.getAddress());
    operator.setStatus(EnumUserStatus.ACTIVE.name());
    operator.setFullname(request.getFullname());
    operator.setIsRoot(false);

    Operator _operator = operatorRepository.save(operator);
    return _operator;
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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.OPERATOR_NOT_FOUND));
    return operator;
  }

  @Override
  public Operator getOperatorByUsername(String username) {
    Operator operator = operatorRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.OPERATOR_NOT_FOUND));
    return operator;
  }

  @Override
  public Operator editOperator(Long id, Map<String, Object> updates) {
    Operator operator = operatorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.OPERATOR_NOT_FOUND));

    String email = String.valueOf(updates.get("email"));
    if (updates.get("email") != null && !Tool.isEqual(operator.getEmail(), email)) {
      if (!userRepository.existsByEmail(email)) {
        operator.setEmail(email);
      } else {
        throw new DuplicateRecordException(ErrorConstant.USER_ALREADY_EXISTS);
      }
    }

    String phone = String.valueOf(updates.get("phone"));
    if (updates.get("phone") != null && !Tool.isEqual(operator.getPhone(), phone)) {
      if (!userRepository.existsByPhone(phone)) {
        operator.setPhone(phone);
      } else {
        throw new DuplicateRecordException(ErrorConstant.USER_PHONE_ALREADY_EXISTS);
      }
    }

    String address = String.valueOf(updates.get("address"));
    if (updates.get("address") != null && !Tool.isEqual(operator.getAddress(), address)) {
      operator.setAddress(address);
    }

    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(operator.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status);
      if (eStatus == null) {
        throw new NotFoundException(ErrorConstant.USER_STATUS_NOT_FOUND);
      }
      operator.setStatus(eStatus.name());
    }

    String fullname = String.valueOf(updates.get("fullname"));
    if (updates.get("fullname") != null && !Tool.isEqual(operator.getFullname(), fullname)) {
      operator.setFullname(fullname);
    }

    Operator _operator = operatorRepository.save(operator);
    return _operator;
  }

  @Override
  public void removeOperator(Long id) {

    if (operatorRepository.existsById(id)) {
      operatorRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.OPERATOR_NOT_FOUND);
    }
  }

}
