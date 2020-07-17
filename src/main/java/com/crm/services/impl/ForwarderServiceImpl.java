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
import com.crm.models.Booking;
import com.crm.models.Forwarder;
import com.crm.models.Outbound;
import com.crm.models.Role;
import com.crm.payload.request.ForwarderRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.RoleRepository;
import com.crm.repository.SupplierRepository;
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
  private SupplierRepository supplierRepository;

  @Autowired
  private ForwarderRepository forwarderRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Override
  public Forwarder createForwarder(SupplierRequest request) {
    if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())
        || userRepository.existsByPhone(request.getPhone())
        || supplierRepository.existsByCompanyCode(request.getCompanyCode())) {
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
    forwarder.setRatingValue(0D);

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
  public Forwarder editForwarder(Long id, Map<String, Object> updates) {
    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));

    /*
     * String password = String.valueOf( updates.get("password")); if (password !=
     * null) { String encoder = passwordEncoder.encode(password);
     * forwarder.setPassword(encoder); }
     */

    String email = String.valueOf(updates.get("email"));
    if (!Tool.isEqual(forwarder.getEmail(), email)) {
      forwarder.setEmail(email);
    }

    String phone = String.valueOf(updates.get("phone"));
    if (!Tool.isEqual(forwarder.getPhone(), phone) && !userRepository.existsByPhone(phone)) {
      forwarder.setPhone(phone);
    } else {
      throw new DuplicateRecordException("Phone number has been existed.");
    }

    String address = String.valueOf(updates.get("address"));
    if (!Tool.isEqual(forwarder.getAddress(), address)) {
      forwarder.setAddress(address);
    }

    String status = String.valueOf(updates.get("status"));
    if (!Tool.isEqual(forwarder.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status);
      forwarder.setStatus(eStatus.name());
    }

    String website = String.valueOf(updates.get("website"));
    if (!Tool.isEqual(forwarder.getWebsite(), website)) {
      forwarder.setWebsite(website);
    }

    String contactPerson = String.valueOf(updates.get("contactPerson"));
    if (!Tool.isEqual(forwarder.getContactPerson(), contactPerson)) {
      forwarder.setContactPerson(contactPerson);
    }

    String companyName = String.valueOf(updates.get("companyName"));
    if (!Tool.isEqual(forwarder.getCompanyName(), companyName)) {
      forwarder.setCompanyName(companyName);
    }

    String companyCode = String.valueOf(updates.get("companyCode"));
    if (!Tool.isEqual(forwarder.getCompanyCode(), companyCode)
        && !supplierRepository.existsByCompanyCode(companyCode)) {
      forwarder.setCompanyCode(companyCode);
    } else {
      throw new DuplicateRecordException("Company code has been existed.");
    }

    String companyDescription = String.valueOf(updates.get("companyDescription"));
    if (!Tool.isEqual(forwarder.getCompanyDescription(), companyDescription)) {
      forwarder.setCompanyDescription(companyDescription);
    }

    String tin = String.valueOf(updates.get("tin"));
    if (!Tool.isEqual(forwarder.getTin(), tin)) {
      forwarder.setTin(tin);
    }

    String fax = String.valueOf(updates.get("fax"));
    if (!Tool.isEqual(forwarder.getFax(), fax)) {
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

  @Override
  public Page<Forwarder> findForwardersByOutbound(Long id, PaginationRequest request) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    String forwarder = outbound.getShippingLine().getCompanyCode();
    String containerType = outbound.getContainerType().getName();
    Booking booking = outbound.getBooking();

    Page<Forwarder> forwarders = forwarderRepository.findByOutbound(forwarder, containerType, outbound.getPackingTime(),
        booking.getCutOffTime(), pageRequest);
    return forwarders;
  }

}
