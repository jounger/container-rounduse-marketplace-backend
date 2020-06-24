package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Forwarder;
import com.crm.models.dto.ForwarderDto;

public class ForwarderMapper {
  
  public static ForwarderDto toForwarderDto(Forwarder forwarder) {
    ForwarderDto forwarderDto = new ForwarderDto();
    forwarderDto.setId(forwarder.getId());
    forwarderDto.setUsername(forwarder.getUsername());
    forwarderDto.setAddress(forwarder.getAddress());
    forwarderDto.setEmail(forwarder.getEmail());
    forwarderDto.setPhone(forwarder.getPhone());
    forwarderDto.setStatus(forwarder.getStatus());

    Set<String> forwarderRoles = new HashSet<>();
    forwarder.getRoles().forEach(role -> forwarderRoles.add(RoleMapper.toRoleDto(role).getName()));
    forwarderDto.setRoles(forwarderRoles);

    forwarderDto.setWebsite(forwarder.getWebsite());
    forwarderDto.setContactPerson(forwarder.getContactPerson());
    forwarderDto.setCompanyName(forwarder.getCompanyName());
    forwarderDto.setCompanyCode(forwarder.getCompanyCode());
    forwarderDto.setCompanyDescription(forwarder.getCompanyDescription());
    forwarderDto.setCompanyAddress(forwarder.getCompanyAddress());
    forwarderDto.setTin(forwarder.getTin());
    forwarderDto.setFax(forwarder.getFax());
    forwarderDto.setRatingValue(forwarder.getRatingValue());
    return forwarderDto;
  }
}
