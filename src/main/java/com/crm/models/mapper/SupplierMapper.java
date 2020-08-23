package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Supplier;
import com.crm.models.dto.SupplierDto;

public class SupplierMapper {

  public static SupplierDto toSupplierDto(Supplier supplier) {
    if (supplier == null) {
      return null;
    }

    SupplierDto supplierDto = new SupplierDto();
    supplierDto.setId(supplier.getId());
    supplierDto.setUsername(supplier.getUsername());
    supplierDto.setAddress(supplier.getAddress());
    supplierDto.setEmail(supplier.getEmail());
    supplierDto.setPhone(supplier.getPhone());
    supplierDto.setStatus(supplier.getStatus());
    supplierDto.setProfileImagePath(supplier.getProfileImagePath());

    Set<String> supplierRoles = new HashSet<>();
    supplier.getRoles().forEach(role -> supplierRoles.add(RoleMapper.toRoleDto(role).getName()));
    supplierDto.setRoles(supplierRoles);

    supplierDto.setWebsite(supplier.getWebsite());
    supplierDto.setFullname(supplier.getFullname());
    supplierDto.setCompanyName(supplier.getCompanyName());
    supplierDto.setCompanyCode(supplier.getCompanyCode());
    supplierDto.setCompanyDescription(supplier.getCompanyDescription());
    supplierDto.setCompanyAddress(supplier.getCompanyAddress());
    supplierDto.setTin(supplier.getTin());
    supplierDto.setFax(supplier.getFax());
    supplierDto.setBrcScanPath(supplier.getProfileImagePath());
    supplierDto.setRatingCount(supplier.getReceivedRatings().size());

    return supplierDto;
  }
}
