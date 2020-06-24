package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Merchant;
import com.crm.models.dto.MerchantDto;

public class MerchantMapper {
  
  public static MerchantDto toMerchantDto(Merchant merchant) {
    MerchantDto merchantDto = new MerchantDto();
    merchantDto.setId(merchant.getId());
    merchantDto.setUsername(merchant.getUsername());
    merchantDto.setAddress(merchant.getAddress());
    merchantDto.setEmail(merchant.getEmail());
    merchantDto.setPhone(merchant.getPhone());
    merchantDto.setStatus(merchant.getStatus());

    Set<String> merchantRoles = new HashSet<>();
    merchant.getRoles().forEach(role -> merchantRoles.add(RoleMapper.toRoleDto(role).getName()));
    merchantDto.setRoles(merchantRoles);

    merchantDto.setWebsite(merchant.getWebsite());
    merchantDto.setContactPerson(merchant.getContactPerson());
    merchantDto.setCompanyName(merchant.getCompanyName());
    merchantDto.setRatingValue(merchant.getRatingValue());
    return merchantDto;
  }
}
