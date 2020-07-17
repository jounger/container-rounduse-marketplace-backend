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
     * String password = (String) updates.get("password"); if (password != null) {
     * String encoder = passwordEncoder.encode(password);
     * merchant.setPassword(encoder); }
     */

    String email = (String) updates.get("email");
    if (!Tool.isEqual(merchant.getEmail(), email)) {
      if(!userRepository.existsByEmail(email)) {
        merchant.setEmail(email);
      }else {
        throw new DuplicateRecordException("Email has been existed.");
      }
    }

    String phone = (String) updates.get("phone");
    if (!Tool.isEqual(merchant.getPhone(), phone)) {
      if (!userRepository.existsByPhone(phone)) {
        merchant.setPhone(phone);
      } else {
        throw new DuplicateRecordException("Phone number has been existed.");
      }
    }

    String address = (String) updates.get("address");
    if (!Tool.isEqual(merchant.getAddress(), address)) {
      merchant.setAddress(address);
    }

    String status = (String) updates.get("status");
    if (!Tool.isEqual(merchant.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status);
      merchant.setStatus(eStatus.name());
    }

    String website = (String) updates.get("website");
    if (!Tool.isEqual(merchant.getWebsite(), website)) {
      merchant.setWebsite(website);
    }

    String contactPerson = (String) updates.get("contactPerson");
    if (!Tool.isEqual(merchant.getContactPerson(), contactPerson)) {
      merchant.setContactPerson(contactPerson);
    }

    String companyName = (String) updates.get("companyName");
    if (!Tool.isEqual(merchant.getCompanyName(), companyName)) {
      merchant.setCompanyName(companyName);
    }

    String companyCode = (String) updates.get("companyCode");
    if (!Tool.isEqual(merchant.getCompanyCode(), companyCode)) {
      if (!supplierRepository.existsByCompanyCode(companyCode)) {
        merchant.setCompanyCode(companyCode);
      } else {
        throw new DuplicateRecordException("Company code has been existed.");
      }
    }

    String companyDescription = (String) updates.get("companyDescription");
    if (!Tool.isEqual(merchant.getCompanyDescription(), companyDescription)) {
      merchant.setCompanyDescription(companyDescription);
    }

    String tin = (String) updates.get("tin");
    if (!Tool.isEqual(merchant.getTin(), tin)) {
      merchant.setTin(tin);
    }

    String fax = (String) updates.get("fax");
    if (!Tool.isEqual(merchant.getFax(), fax)) {
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
