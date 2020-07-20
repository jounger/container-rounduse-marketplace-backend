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

import com.crm.common.Tool;
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
import com.crm.repository.SupplierRepository;
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
  private SupplierRepository supplierRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Merchant createMerchant(SupplierRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())
        || supplierRepository.existsByCompanyCode(request.getCompanyCode())) {
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
    merchant.setRatingValue(0D);

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
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return merchants;
  }

  @Override
  public Merchant updateMerchant(MerchantRequest request) {
    Merchant merchant = merchantRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Merchant is not found."));

    /*
     * String encoder = passwordEncoder.encode(request.getPassword());
     * merchant.setPassword(encoder);
     */

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

    /*
     * String password = String.valueOf( updates.get("password")); if (password !=
     * null) { String encoder = passwordEncoder.encode(password);
     * merchant.setPassword(encoder); }
     */

 
    String email = String.valueOf(updates.get("email"));
    if (updates.get("email") != null && email != null && UserServiceImpl.isEmailChange(email, merchant)
        && !email.isEmpty()) {
      merchant.setEmail(email);
    }

    String phone = String.valueOf(updates.get("phone"));
    if (updates.get("phone") != null && !Tool.isEqual(merchant.getPhone(), phone)) {
      if (userRepository.existsByPhone(phone)) {
        throw new DuplicateRecordException("Phone number has been existed.");
      }
      merchant.setPhone(phone);
    }

    String address = String.valueOf(updates.get("address"));
    if (updates.get("address") != null && !Tool.isEqual(merchant.getAddress(), address)) {
      merchant.setAddress(address);
    }

    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(merchant.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status);
      merchant.setStatus(eStatus.name());
    }

    String website = String.valueOf(updates.get("website"));
    if (updates.get("website") != null && !Tool.isEqual(merchant.getWebsite(), website)) {
      merchant.setWebsite(website);
    }

    String contactPerson = String.valueOf(updates.get("contactPerson"));
    if (updates.get("contactPerson") != null && !Tool.isEqual(merchant.getContactPerson(), contactPerson)) {
      merchant.setContactPerson(contactPerson);
    }

    String companyName = String.valueOf(updates.get("companyName"));
    if (updates.get("companyName") != null && !Tool.isEqual(merchant.getCompanyName(), companyName)) {
      merchant.setCompanyName(companyName);
    }

    String companyCode = String.valueOf(updates.get("companyCode"));
    if (updates.get("companyCode") != null && !Tool.isEqual(merchant.getCompanyCode(), companyCode)) {
      if (supplierRepository.existsByCompanyCode(companyCode)) {
        throw new DuplicateRecordException("Company code has been existed.");
      }
      merchant.setCompanyCode(companyCode);
    }

    String companyDescription = String.valueOf(updates.get("companyDescription"));
    if (updates.get("companyDescription") != null
        && !Tool.isEqual(merchant.getCompanyDescription(), companyDescription)) {
      merchant.setCompanyDescription(companyDescription);
    }

    String tin = String.valueOf(updates.get("tin"));
    if (updates.get("tin") != null && !Tool.isEqual(merchant.getTin(), tin)) {
      merchant.setTin(tin);
    }

    String fax = String.valueOf(updates.get("fax"));
    if (updates.get("fax") != null && !Tool.isEqual(merchant.getFax(), fax)) {
      merchant.setFax(fax);
    }

    merchantRepository.save(merchant);
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
