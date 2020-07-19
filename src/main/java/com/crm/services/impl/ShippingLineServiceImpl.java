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
      throw new DuplicateRecordException("Error: User has been existed");
    }
    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setUsername(request.getUsername());

    String encoder = passwordEncoder.encode(request.getPassword());
    shippingLine.setPassword(encoder);

    Role userRole = roleRepository.findByName("ROLE_SHIPPINGLINE")
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    shippingLine.getRoles().add(userRole);

    shippingLine.setEmail(request.getEmail());
    shippingLine.setPhone(request.getPhone());
    shippingLine.setAddress(request.getAddress());
    shippingLine.setStatus(EnumUserStatus.ACTIVE.name());
    shippingLine.setWebsite(request.getWebsite());
    shippingLine.setContactPerson(request.getContactPerson());
    shippingLine.setCompanyName(request.getCompanyName());
    shippingLine.setCompanyCode(request.getCompanyCode());
    shippingLine.setCompanyDescription(request.getCompanyDescription());
    shippingLine.setCompanyAddress(request.getCompanyAddress());
    shippingLine.setTin(request.getTin());
    shippingLine.setFax(request.getFax());

    shippingLineRepository.save(shippingLine);

    return shippingLine;
  }

  @Override
  public ShippingLine getShippingLine(Long id) {
    ShippingLine shippingLine = shippingLineRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Shipping Line is not found."));
    return shippingLine;
  }

  @Override
  public Page<ShippingLine> getShippingLines(PaginationRequest request) {
    Page<ShippingLine> shippingLines = shippingLineRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return shippingLines;
  }

  @Override
  public ShippingLine updateShippingLine(ShippingLineRequest request) {
    ShippingLine shippingLine = shippingLineRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Shipping Line is not found."));

    /*
     * String encoder = passwordEncoder.encode(request.getPassword());
     * shippingLine.setPassword(encoder);
     */

    Role userRole = roleRepository.findByName(request.getRoles().iterator().next())
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    shippingLine.getRoles().add(userRole);

    if (UserServiceImpl.isEmailChange(request.getEmail(), shippingLine)) {
      shippingLine.setEmail(request.getEmail());
    }

    shippingLine.setPhone(request.getPhone());
    shippingLine.setAddress(request.getAddress());

    EnumUserStatus status = EnumUserStatus.findByName(request.getStatus());
    shippingLine.setStatus(status.name());

    shippingLine.setWebsite(request.getWebsite());
    shippingLine.setContactPerson(request.getContactPerson());
    shippingLine.setCompanyName(request.getCompanyName());
    if (!shippingLine.getCompanyCode().equals(request.getCompanyCode())
        && !supplierRepository.existsByCompanyCode(request.getCompanyCode())) {
      shippingLine.setCompanyCode(request.getCompanyCode());
    } else {
      throw new DuplicateRecordException("Company code has been existed.");
    }

    shippingLine.setCompanyDescription(request.getCompanyDescription());
    shippingLine.setTin(request.getTin());
    shippingLine.setFax(request.getFax());
    shippingLineRepository.save(shippingLine);

    return shippingLine;
  }

  @Override
  public ShippingLine editShippingLine(Long id, Map<String, Object> updates) {
    ShippingLine shippingLine = shippingLineRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Shipping Line is not found."));

    /*
     * String password = String.valueOf( updates.get("password")); if (password !=
     * null && !password.isEmpty()) { String encoder =
     * passwordEncoder.encode(password); shippingLine.setPassword(encoder); }
     */

    String email = String.valueOf(updates.get("email"));
    if (updates.get("email") != null && !Tool.isEqual(shippingLine.getEmail(), email)) {
      if (!userRepository.existsByEmail(email)) {
        shippingLine.setEmail(email);
      } else {
        throw new DuplicateRecordException("Email has been existed.");
      }
    }

    String phone = String.valueOf(updates.get("phone"));
    if (updates.get("phone") != null && !Tool.isEqual(shippingLine.getPhone(), phone)
        && !userRepository.existsByPhone(phone)) {
      shippingLine.setPhone(phone);
    } else {
      throw new DuplicateRecordException("Phone number has been existed.");
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

    String contactPerson = String.valueOf(updates.get("contactPerson"));
    if (updates.get("contactPerson") != null && !Tool.isEqual(shippingLine.getContactPerson(), contactPerson)) {
      shippingLine.setContactPerson(contactPerson);
    }

    String companyName = String.valueOf(updates.get("companyName"));
    if (updates.get("companyName") != null && !Tool.isEqual(shippingLine.getCompanyName(), companyName)) {
      shippingLine.setCompanyName(companyName);
    }

    String companyCode = String.valueOf(updates.get("companyCode"));
    if (updates.get("companyCode") != null && !Tool.isEqual(shippingLine.getCompanyCode(), companyCode)
        && !supplierRepository.existsByCompanyCode(companyCode)) {
      shippingLine.setCompanyCode(companyCode);
    } else {
      throw new DuplicateRecordException("Company code has been existed.");
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

    shippingLineRepository.save(shippingLine);

    return shippingLine;
  }

  @Override
  public void removeShippingLine(Long id) {
    if (shippingLineRepository.existsById(id)) {
      shippingLineRepository.deleteById(id);
    } else {
      throw new NotFoundException("Shipping Line is not found.");
    }

  }

}
