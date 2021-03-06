package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Role;
import com.crm.models.ShippingLine;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingLineRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ShippingLineService;

@Service
public class ShippingLineServiceImpl implements ShippingLineService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ShippingLineRepository shippingLineRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public ShippingLine createShippingLine(ShippingLineRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())
        || supplierRepository.existsByCompanyCode(request.getCompanyCode())) {
      throw new DuplicateRecordException(ErrorMessage.USER_ALREADY_EXISTS);
    }
    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setUsername(request.getUsername());

    String encoder = passwordEncoder.encode(request.getPassword());
    shippingLine.setPassword(encoder);

    Role userRole = roleRepository.findByName("ROLE_SHIPPINGLINE")
        .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
    shippingLine.getRoles().add(userRole);

    shippingLine.setEmail(request.getEmail());
    shippingLine.setPhone(request.getPhone());
    shippingLine.setAddress(request.getAddress());
    shippingLine.setStatus(EnumUserStatus.ACTIVE.name());
    shippingLine.setWebsite(request.getWebsite());
    shippingLine.setFullname(request.getFullname());
    shippingLine.setCompanyName(request.getCompanyName());

    if (supplierRepository.existsByCompanyCode(request.getCompanyCode())) {
      throw new DuplicateRecordException(ErrorMessage.COMPANY_CODE_ALREADY_EXISTS);
    }
    shippingLine.setCompanyCode(request.getCompanyCode());

    shippingLine.setCompanyDescription(request.getCompanyDescription());
    shippingLine.setCompanyAddress(request.getCompanyAddress());

    if (supplierRepository.existsByTin(request.getTin())) {
      throw new DuplicateRecordException(ErrorMessage.TIN_DUPLICATE);
    }
    shippingLine.setTin(request.getTin());

    if (supplierRepository.existsByFax(request.getFax())) {
      throw new DuplicateRecordException(ErrorMessage.FAX_DUPLICATE);
    }
    shippingLine.setFax(request.getFax());

    ShippingLine _shippingLine = shippingLineRepository.save(shippingLine);
    return _shippingLine;
  }

  @Override
  public ShippingLine getShippingLine(Long id) {
    ShippingLine shippingLine = shippingLineRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPINGLINE_NOT_FOUND));
    return shippingLine;
  }

  @Override
  public Page<ShippingLine> getShippingLines(PaginationRequest request) {
    Page<ShippingLine> shippingLines = shippingLineRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return shippingLines;
  }

  @Override
  public ShippingLine editShippingLine(Long id, Map<String, Object> updates) {
    ShippingLine shippingLine = shippingLineRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPINGLINE_NOT_FOUND));

    String email = String.valueOf(updates.get("email"));
    if (updates.get("email") != null && !Tool.isEqual(shippingLine.getEmail(), email)) {
      if (!userRepository.existsByEmail(email)) {
        shippingLine.setEmail(email);
      } else {
        throw new DuplicateRecordException(ErrorMessage.USER_EMAIL_ALREADY_EXISTS);
      }
    }

    String phone = String.valueOf(updates.get("phone"));
    if (updates.get("phone") != null && !Tool.isEqual(shippingLine.getPhone(), phone)) {
      if (userRepository.existsByPhone(phone)) {
        throw new DuplicateRecordException(ErrorMessage.USER_PHONE_ALREADY_EXISTS);
      }
      shippingLine.setPhone(phone);
    }

    String address = String.valueOf(updates.get("address"));
    if (updates.get("address") != null && !Tool.isEqual(shippingLine.getAddress(), address)) {
      shippingLine.setAddress(address);
    }

    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(shippingLine.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status);
      shippingLine.setStatus(eStatus.name());
    }

    String website = String.valueOf(updates.get("website"));
    if (updates.get("website") != null && !Tool.isEqual(shippingLine.getWebsite(), website)) {
      shippingLine.setWebsite(website);
    }

    String fullname = String.valueOf(updates.get("fullname"));
    if (updates.get("fullname") != null && !Tool.isEqual(shippingLine.getFullname(), fullname)) {
      shippingLine.setFullname(fullname);
    }

    String companyName = String.valueOf(updates.get("companyName"));
    if (updates.get("companyName") != null && !Tool.isEqual(shippingLine.getCompanyName(), companyName)) {
      shippingLine.setCompanyName(companyName);
    }

    String companyCode = String.valueOf(updates.get("companyCode"));
    if (updates.get("companyCode") != null && !Tool.isEqual(shippingLine.getCompanyCode(), companyCode)) {
      if (supplierRepository.existsByCompanyCode(companyCode)) {
        throw new DuplicateRecordException(ErrorMessage.COMPANY_CODE_ALREADY_EXISTS);
      }
      shippingLine.setCompanyCode(companyCode);
    }

    String companyDescription = String.valueOf(updates.get("companyDescription"));
    if (updates.get("companyDescription") != null
        && !Tool.isEqual(shippingLine.getCompanyDescription(), companyDescription)) {
      shippingLine.setCompanyDescription(companyDescription);
    }

    String tin = String.valueOf(updates.get("tin"));
    if (updates.get("tin") != null && !Tool.isEqual(shippingLine.getTin(), tin)) {
      shippingLine.setTin(tin);
    }

    String fax = String.valueOf(updates.get("fax"));
    if (updates.get("fax") != null && !Tool.isEqual(shippingLine.getFax(), fax)) {
      shippingLine.setFax(fax);
    }

    ShippingLine _shippingLine = shippingLineRepository.save(shippingLine);
    return _shippingLine;
  }

  @Override
  public void removeShippingLine(Long id) {
    if (shippingLineRepository.existsById(id)) {
      shippingLineRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.SHIPPINGLINE_NOT_FOUND);
    }

  }

}
