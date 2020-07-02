package com.crm.services.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Forwarder;
import com.crm.models.Role;
import com.crm.payload.request.ForwarderRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ForwarderService;

@Service
public class ForwarderServiceImpl implements ForwarderService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ForwarderRepository forwarderRepository;

  @Override
  public Forwarder createForwarder(SupplierRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException("Error: User has been existed");
    }
    Forwarder forwarder = new Forwarder();
    forwarder.setUsername(request.getUsername());

    String encoder = passwordEncoder.encode(request.getPassword());
    forwarder.setPassword(encoder);

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName("ROLE_FORWARDER")
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    roles.add(userRole);
    forwarder.setRoles(roles);

    forwarder.setEmail(request.getEmail());
    forwarder.setPhone(request.getPhone());
    forwarder.setAddress(request.getAddress());
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite(request.getWebsite());
    forwarder.setCompanyName(request.getCompanyName());
    forwarder.setCompanyCode(request.getCompanyCode());
    forwarder.setCompanyDescription(request.getCompanyDescription());
    forwarder.setCompanyAddress(request.getCompanyAddress());
    forwarder.setContactPerson(request.getContactPerson());
    forwarder.setTin(request.getTin());
    forwarder.setFax(request.getFax());

    forwarderRepository.save(forwarder);

    return forwarder;
  }

  @Override
  public Forwarder getForwarder(Long id) {
    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));
    return forwarder;
  }

  @Override
  public Page<Forwarder> getForwarders(PaginationRequest request) {
    Page<Forwarder> forwarders = forwarderRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return forwarders;
  }

  @Override
  public Forwarder updateForwarder(ForwarderRequest request) {
    Forwarder forwarder = forwarderRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));

    /*
     * String encoder = passwordEncoder.encode(request.getPassword());
     * forwarder.setPassword(encoder);
     */

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName("ROLE_FORWARDER")
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    roles.add(userRole);
    forwarder.setRoles(roles);

    if (UserServiceImpl.isEmailChange(request.getEmail(), forwarder)) {
      forwarder.setEmail(request.getEmail());
    }

    forwarder.setPhone(request.getPhone());
    forwarder.setAddress(request.getAddress());
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite(request.getWebsite());
    forwarder.setCompanyName(request.getCompanyName());
//    forwarder.setCompanyCode(request.getCompanyCode());
    forwarder.setCompanyDescription(request.getCompanyDescription());
    forwarder.setCompanyAddress(request.getCompanyAddress());
    forwarder.setContactPerson(request.getContactPerson());
    forwarder.setTin(request.getTin());
    forwarder.setFax(request.getFax());

    forwarderRepository.save(forwarder);

    return forwarder;
  }

  @Override
  public Forwarder editForwarder(Long id, Map<String, Object> updates) {
    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));

    /*
     * String password = (String) updates.get("password"); if (password != null) {
     * String encoder = passwordEncoder.encode(password);
     * forwarder.setPassword(encoder); }
     */

    String email = (String) updates.get("email");
    if (email != null && UserServiceImpl.isEmailChange(email, forwarder)) {
      forwarder.setEmail(email);
    }

    String phone = (String) updates.get("phone");
    if (phone != null) {
      forwarder.setPhone(phone);
    }

    String address = (String) updates.get("address");
    if (address != null && !address.isEmpty()) {
      forwarder.setAddress(address);
    }

    String website = (String) updates.get("website");
    if (website != null && !website.isEmpty()) {
      forwarder.setWebsite(website);
    }

    String contactPerson = (String) updates.get("contactPerson");
    if (contactPerson != null && !contactPerson.isEmpty()) {
      forwarder.setContactPerson(contactPerson);
    }

    String companyName = (String) updates.get("companyName");
    if (companyName != null && !companyName.isEmpty()) {
      forwarder.setCompanyName(companyName);
    }

//    String companyCode = (String) updates.get("companyCode");
//    if (companyCode != null) {
//      forwarder.setCompanyCode(companyCode);
//    }

    String companyDescription = (String) updates.get("companyDescription");
    if (companyDescription != null && !companyDescription.isEmpty()) {
      forwarder.setCompanyDescription(companyDescription);
    }

    String companyAddress = (String) updates.get("companyAddress");
    if (companyAddress != null && !companyAddress.isEmpty()) {
      forwarder.setCompanyAddress(companyAddress);
    }

    String tin = (String) updates.get("tin");
    if (tin != null && !tin.isEmpty()) {
      forwarder.setTin(tin);
    }

    String fax = (String) updates.get("fax");
    if (fax != null && !fax.isEmpty()) {
      forwarder.setFax(fax);
    }

    forwarderRepository.save(forwarder);
    return forwarder;
  }

  @Override
  public void removeForwarder(Long id) {
    if (forwarderRepository.existsById(id)) {
      forwarderRepository.deleteById(id);
    } else {
      throw new NotFoundException("Forwarder is not found.");
    }

  }

}
