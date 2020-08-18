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

import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Booking;
import com.crm.models.Forwarder;
import com.crm.models.Outbound;
import com.crm.models.Role;
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
      throw new DuplicateRecordException(ErrorMessage.USER_ALREADY_EXISTS);
    }
    Forwarder forwarder = new Forwarder();
    forwarder.setUsername(request.getUsername());

    String encoder = passwordEncoder.encode(request.getPassword());
    forwarder.setPassword(encoder);

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName("ROLE_FORWARDER")
        .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
    roles.add(userRole);
    forwarder.setRoles(roles);

    forwarder.setEmail(request.getEmail());
    forwarder.setPhone(request.getPhone());
    forwarder.setAddress(request.getAddress());
    forwarder.setStatus(EnumUserStatus.PENDING.name());
    forwarder.setWebsite(request.getWebsite());
    forwarder.setCompanyName(request.getCompanyName());
    if (supplierRepository.existsByCompanyCode(request.getCompanyCode())) {
      throw new DuplicateRecordException(ErrorMessage.COMPANY_CODE_ALREADY_EXISTS);
    }
    forwarder.setCompanyCode(request.getCompanyCode());
    forwarder.setCompanyDescription(request.getCompanyDescription());
    forwarder.setCompanyAddress(request.getCompanyAddress());
    forwarder.setFullname(request.getFullname());
    if (supplierRepository.existsByTin(request.getTin())) {
      throw new DuplicateRecordException(ErrorMessage.TIN_DUPLICATE);
    }
    forwarder.setTin(request.getTin());
    if (supplierRepository.existsByFax(request.getFax())) {
      throw new DuplicateRecordException(ErrorMessage.FAX_DUPLICATE);
    }
    forwarder.setFax(request.getFax());
    forwarder.setRatingValue(0D);

    Forwarder _forwarder = forwarderRepository.save(forwarder);
    return _forwarder;
  }

  @Override
  public Forwarder getForwarder(Long id) {
    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND));
    return forwarder;
  }

  @Override
  public Page<Forwarder> getForwarders(PaginationRequest request) {
    Page<Forwarder> forwarders = forwarderRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return forwarders;
  }

  @Override
  public Forwarder editForwarder(Long id, Map<String, Object> updates) {
    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND));

    String email = String.valueOf(updates.get("email"));
    if (updates.get("email") != null && !Tool.isEqual(forwarder.getEmail(), email)) {
      if (!userRepository.existsByEmail(email)) {
        forwarder.setEmail(email);
      } else {
        throw new DuplicateRecordException(ErrorMessage.USER_EMAIL_ALREADY_EXISTS);
      }
    }

    String phone = String.valueOf(updates.get("phone"));
    if (updates.get("phone") != null && !Tool.isEqual(forwarder.getPhone(), phone)) {
      if (userRepository.existsByPhone(phone)) {
        throw new DuplicateRecordException(ErrorMessage.USER_PHONE_ALREADY_EXISTS);
      }
      forwarder.setPhone(phone);
    }

    String address = String.valueOf(updates.get("address"));
    if (updates.get("address") != null && !Tool.isEqual(forwarder.getAddress(), address)) {
      forwarder.setAddress(address);
    }

    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(forwarder.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status);
      forwarder.setStatus(eStatus.name());
    }

    String website = String.valueOf(updates.get("website"));
    if (updates.get("website") != null && !Tool.isEqual(forwarder.getWebsite(), website)) {
      forwarder.setWebsite(website);
    }

    String fullname = String.valueOf(updates.get("fullname"));
    if (updates.get("fullname") != null && !Tool.isEqual(forwarder.getFullname(), fullname)) {
      forwarder.setFullname(fullname);
    }

    String companyName = String.valueOf(updates.get("companyName"));
    if (updates.get("companyName") != null && !Tool.isEqual(forwarder.getCompanyName(), companyName)) {
      forwarder.setCompanyName(companyName);
    }

    String companyCode = String.valueOf(updates.get("companyCode"));
    if (updates.get("companyCode") != null && !Tool.isEqual(forwarder.getCompanyCode(), companyCode)) {
      if (supplierRepository.existsByCompanyCode(companyCode)) {
        throw new DuplicateRecordException(ErrorMessage.COMPANY_CODE_ALREADY_EXISTS);
      }
      forwarder.setCompanyCode(companyCode);
    }

    String companyDescription = String.valueOf(updates.get("companyDescription"));
    if (updates.get("companyDescription") != null
        && !Tool.isEqual(forwarder.getCompanyDescription(), companyDescription)) {
      forwarder.setCompanyDescription(companyDescription);
    }

    String tin = String.valueOf(updates.get("tin"));
    if (updates.get("tin") != null && !Tool.isEqual(forwarder.getTin(), tin)) {
      if (supplierRepository.existsByTin(tin)) {
        throw new DuplicateRecordException(ErrorMessage.TIN_DUPLICATE);
      }
      forwarder.setTin(tin);
    }

    String fax = String.valueOf(updates.get("fax"));
    if (updates.get("fax") != null && !Tool.isEqual(forwarder.getFax(), fax)) {
      if (supplierRepository.existsByFax(fax)) {
        throw new DuplicateRecordException(ErrorMessage.FAX_DUPLICATE);
      }
      forwarder.setFax(fax);
    }

    Forwarder _forwarder = forwarderRepository.save(forwarder);
    return _forwarder;
  }

  @Override
  public void removeForwarder(Long id) {
    if (forwarderRepository.existsById(id)) {
      forwarderRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND);
    }

  }

  @Override
  public Page<Forwarder> findForwardersByOutbound(Long id, PaginationRequest request) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND));
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
