package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUserStatus;
import com.crm.exception.NotFoundException;
import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.repository.SupplierRepository;
import com.crm.services.SupplierService;

@Service
public class SupplierServiceImpl implements SupplierService {

  @Autowired
  private SupplierRepository supplierRepository;

  @Override
  public Page<Supplier> getSuppliers(PaginationRequest request) {
    Page<Supplier> pages = supplierRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public Supplier getSupplier(String username) {
    Supplier supplier = supplierRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("User is not found"));
    return supplier;
  }

  @Override
  public Page<Supplier> getSuppliersByStatus(PaginationRequest request) {
    EnumUserStatus userStatus = EnumUserStatus.findByName(request.getStatus());
    Page<Supplier> pages = supplierRepository.findByStatus(userStatus, PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public Supplier editSupplierStatus(SupplierRequest request) {
    Supplier supplier = supplierRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new NotFoundException("User is not found"));
    EnumUserStatus status = EnumUserStatus.findByName(request.getStatus().toUpperCase());
    supplier.setStatus(status);
    supplierRepository.save(supplier);
    return supplier;
  }

}
