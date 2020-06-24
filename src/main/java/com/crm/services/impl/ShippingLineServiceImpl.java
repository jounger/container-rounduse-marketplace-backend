package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Role;
import com.crm.models.ShippingLine;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingLineRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.ShippingLineRepository;
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
  private PasswordEncoder passwordEncoder;

  @Override
  public void saveShippingLine(ShippingLineRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
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
        .findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return shippingLines;
  }

  @Override
  public ShippingLine updateShippingLine(ShippingLineRequest request) {
    ShippingLine shippingLine = shippingLineRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Shipping Line is not found."));
    
    String encoder = passwordEncoder.encode(request.getPassword());
    shippingLine.setPassword(encoder);
    
    Role userRole = roleRepository.findByName(request.getRoles().iterator().next())
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    shippingLine.getRoles().add(userRole);
    
    shippingLine.setEmail(request.getEmail());
    shippingLine.setPhone(request.getPhone());
    shippingLine.setAddress(request.getAddress());
    
    EnumUserStatus status = EnumUserStatus.findByName(request.getStatus());
    shippingLine.setStatus(status.name());
    
    shippingLine.setWebsite(request.getWebsite());
    shippingLine.setContactPerson(request.getContactPerson());
    shippingLine.setCompanyName(request.getCompanyName());
    shippingLine.setCompanyCode(request.getCompanyCode());
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
    
    String password = (String)updates.get("password");
    if(password != null) {
      String encoder = passwordEncoder.encode(password);
      shippingLine.setPassword(encoder);
    }
    
    String email = (String)updates.get("email");
    if(email != null) {
      shippingLine.setEmail(email);
    }
    
    String phone = (String)updates.get("phone");
    if(phone != null) {
      shippingLine.setPhone(phone);
    }
    
    String address = (String)updates.get("address");
    if(address != null) {
      shippingLine.setAddress(address);
    }
    
    String website = (String)updates.get("website");
    if(website != null) {
      shippingLine.setWebsite(website);
    }
    
    String contactPerson = (String)updates.get("contactPerson");
    if(contactPerson != null) {
      shippingLine.setContactPerson(contactPerson);
    }
    
    String companyName = (String)updates.get("companyName");
    if(companyName != null) {
      shippingLine.setCompanyName(companyName);
    }
    
    String companyCode = (String)updates.get("companyCode");
    if(companyCode != null) {
      shippingLine.setCompanyCode(companyCode);
    }
    
    String companyDescription = (String)updates.get("companyDescription");
    if(companyDescription != null) {
      shippingLine.setCompanyDescription(companyDescription);
    }
    
    String tin = (String)updates.get("tin");
    if(tin != null) {
      shippingLine.setTin(tin);
    }
    
    String fax = (String)updates.get("fax");
    if(fax != null) {
      shippingLine.setFax(fax);
    }
    
    return shippingLine;
  }

  @Override
  public void removeShippingLine(Long id) {
    if(shippingLineRepository.existsById(id)) {
      shippingLineRepository.deleteById(id);
    }else {
      throw new NotFoundException("Shipping Line is not found.");
    }

  }

}
