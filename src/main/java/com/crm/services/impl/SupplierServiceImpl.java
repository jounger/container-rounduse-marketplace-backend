package com.crm.services.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.NotFoundException;
import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.SupplierRepository;
import com.crm.services.SupplierService;
import com.crm.specification.builder.SupplierSpecificationsBuilder;

@Service
public class SupplierServiceImpl implements SupplierService {

  @Autowired
  private SupplierRepository supplierRepository;

  @Override
  public Page<Supplier> getSuppliers(PaginationRequest request) {
    Page<Supplier> pages = supplierRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return pages;
  }

  @Override
  public Supplier getSupplier(String username) {
    Supplier supplier = supplierRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    return supplier;
  }

  @Override
  public Supplier getSupplier(Long id) {
    Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    return supplier;
  }

  @Override
  public Page<Supplier> getSuppliersByStatus(PaginationRequest request) {
    EnumUserStatus userStatus = EnumUserStatus.findByName(request.getStatus().toString());
    if (userStatus == null) {
      throw new NotFoundException(ErrorMessage.USER_STATUS_NOT_FOUND);
    }
    Page<Supplier> pages = supplierRepository.findByStatus(userStatus.name(),
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return pages;
  }

  @Override
  public Page<Supplier> searchSuppliers(PaginationRequest request, String search) {
    SupplierSpecificationsBuilder builder = new SupplierSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Supplier> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Supplier> pages = supplierRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Supplier editSupplier(Map<String, Object> updates, Long id) {
    Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new NotFoundException("User is not found"));
    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(supplier.getStatus(), status)) {
      EnumUserStatus eStatus = EnumUserStatus.findByName(status.toUpperCase());
      if (eStatus == null) {
        throw new NotFoundException(ErrorMessage.USER_STATUS_NOT_FOUND);
      }
      supplier.setStatus(eStatus.name());
    }
    Supplier _supplier = supplierRepository.save(supplier);
    return _supplier;
  }

  @Override
  public Page<Supplier> getSuppliersByRole(PaginationRequest request) {
    List<String> roles = Arrays.asList("ROLE_MERCHANT", "ROLE_FORWARDER");
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Supplier> pages = supplierRepository.findByRole(roles, page);
    return pages;
  }

}
