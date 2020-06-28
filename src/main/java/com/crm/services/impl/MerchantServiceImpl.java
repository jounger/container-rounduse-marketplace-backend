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
import com.crm.models.Merchant;
import com.crm.models.Role;
import com.crm.payload.request.MerchantRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.MerchantRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.MerchantService;

@Service
public class MerchantServiceImpl implements MerchantService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private MerchantRepository merchantRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Merchant createMerchant(SupplierRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())) {
      throw new DuplicateRecordException("Error: User has been existed");
    }
    Merchant merchant = new Merchant();
    merchant.setUsername(request.getUsername());
    merchant.setEmail(request.getEmail());
    merchant.setPhone(request.getPhone());
    merchant.setAddress(request.getAddress());
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite(request.getWebsite());
    merchant.setCompanyName(request.getCompanyName());
    merchant.setCompanyCode(request.getCompanyCode());
    merchant.setCompanyDescription(request.getCompanyDescription());
    merchant.setCompanyAddress(request.getCompanyAddress());
    merchant.setContactPerson(request.getContactPerson());
    merchant.setTin(request.getTin());
    merchant.setFax(request.getFax());

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName("ROLE_MERCHANT")
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    roles.add(userRole);
    merchant.setRoles(roles);

    String encoder = passwordEncoder.encode(request.getPassword());
    merchant.setPassword(encoder);

    merchantRepository.save(merchant);

    return merchant;
  }

  @Override
  public Merchant getMerchant(Long id) {
    Merchant merchant = merchantRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Merchant is not found."));
    return merchant;
  }

  @Override
  public Page<Merchant> getMerchants(PaginationRequest request) {
    Page<Merchant> merchants = merchantRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
    return merchants;
  }

  @Override
  public Merchant updateMerchant(MerchantRequest request) {
    Merchant merchant = merchantRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Merchant is not found."));

    String encoder = passwordEncoder.encode(request.getPassword());
    merchant.setPassword(encoder);

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName("ROLE_FORWARDER")
        .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
    roles.add(userRole);
    merchant.setRoles(roles);

    if (UserServiceImpl.isEmailChange(request.getEmail(), merchant)) {
      merchant.setEmail(request.getEmail());
    }

    merchant.setPhone(request.getPhone());
    merchant.setAddress(request.getAddress());
    merchant.setStatus(EnumUserStatus.PENDING.name());
    merchant.setWebsite(request.getWebsite());
    merchant.setCompanyName(request.getCompanyName());
    merchant.setCompanyCode(request.getCompanyCode());
    merchant.setCompanyDescription(request.getCompanyDescription());
    merchant.setCompanyAddress(request.getCompanyAddress());
    merchant.setContactPerson(request.getContactPerson());
    merchant.setTin(request.getTin());
    merchant.setFax(request.getFax());

    merchantRepository.save(merchant);

    return merchant;
  }

  @Override
  public Merchant editMerchant(Long id, Map<String, Object> updates) {
    Merchant merchant = merchantRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Merchant is not found."));

    String password = (String) updates.get("password");
    if (password != null) {
      String encoder = passwordEncoder.encode(password);
      merchant.setPassword(encoder);
    }

    String email = (String) updates.get("email");
    if (email != null && UserServiceImpl.isEmailChange(email, merchant)) {
      merchant.setEmail(email);
    }

    String phone = (String) updates.get("phone");
    if (phone != null) {
      merchant.setPhone(phone);
    }

    String address = (String) updates.get("address");
    if (address != null) {
      merchant.setAddress(address);
    }

    String website = (String) updates.get("website");
    if (website != null) {
      merchant.setWebsite(website);
    }

    String contactPerson = (String) updates.get("contactPerson");
    if (contactPerson != null) {
      merchant.setContactPerson(contactPerson);
    }

    String companyName = (String) updates.get("companyName");
    if (companyName != null) {
      merchant.setCompanyName(companyName);
    }

    String companyCode = (String) updates.get("companyCode");
    if (companyCode != null) {
      merchant.setCompanyCode(companyCode);
    }

    String companyDescription = (String) updates.get("companyDescription");
    if (companyDescription != null) {
      merchant.setCompanyDescription(companyDescription);
    }

    String companyAddress = (String) updates.get("companyAddress");
    if (companyAddress != null) {
      merchant.setCompanyAddress(companyAddress);
    }

    String tin = (String) updates.get("tin");
    if (tin != null) {
      merchant.setTin(tin);
    }

    String fax = (String) updates.get("fax");
    if (fax != null) {
      merchant.setFax(fax);
    }
    return merchant;
  }

  @Override
  public void removeMerchant(Long id) {
    if (merchantRepository.existsById(id)) {
      merchantRepository.deleteById(id);
    } else {
      throw new NotFoundException("Merchant is not found.");
    }

  }

}
