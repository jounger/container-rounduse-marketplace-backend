package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Supplier;
import com.crm.models.dto.ShippingLineDto;

public class ShippingLineMapper {

  public static ShippingLineDto toShippingLineDto(Supplier shippingLine) {
    if (shippingLine == null) {
      return null;
    }

    ShippingLineDto shippingLineDto = new ShippingLineDto();
    shippingLineDto.setId(shippingLine.getId());
    shippingLineDto.setUsername(shippingLine.getUsername());
    shippingLineDto.setAddress(shippingLine.getAddress());
    shippingLineDto.setEmail(shippingLine.getEmail());
    shippingLineDto.setPhone(shippingLine.getPhone());
    shippingLineDto.setStatus(shippingLine.getStatus());
    shippingLineDto.setProfileImagePath(shippingLine.getProfileImagePath());

    Set<String> shippingLineRoles = new HashSet<>();
    shippingLine.getRoles().forEach(role -> shippingLineRoles.add(RoleMapper.toRoleDto(role).getName()));
    shippingLineDto.setRoles(shippingLineRoles);

    shippingLineDto.setWebsite(shippingLine.getWebsite());
    shippingLineDto.setFullname(shippingLine.getFullname());
    shippingLineDto.setCompanyName(shippingLine.getCompanyName());
    shippingLineDto.setCompanyCode(shippingLine.getCompanyCode());
    shippingLineDto.setCompanyDescription(shippingLine.getCompanyDescription());
    shippingLineDto.setTin(shippingLine.getTin());
    shippingLineDto.setFax(shippingLine.getFax());

    return shippingLineDto;
  }
}
