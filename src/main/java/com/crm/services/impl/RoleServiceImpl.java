package com.crm.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumRole;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;
import com.crm.repository.RoleRepository;
import com.crm.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;
  
  @Override
  public void saveRole(RoleRequest request) {
    List<EnumRole> rolesEnum = Arrays.asList(EnumRole.values());
    int count = 0;
    for(int i = 0; i < rolesEnum.size(); i++) {
      if(request.getName().equalsIgnoreCase(rolesEnum.get(i).name().split("_")[1])) {
        Role role = new Role();
        role.setName(rolesEnum.get(i));
        if(roleRepository.existsByName(role.getName())) {
          throw new DuplicateRecordException("Error: role has been existed");
        }
        count++;
        roleRepository.save(role);
      }
    }
    if(count == 0) {
      throw new NotFoundException("Error: Role is not in bound");
    }
  }

  @Override
  public Page<Role> getRoles(PaginationRequest request) {
    Page<Role> pages = roleRepository.findAll(PageRequest.of(request.getPageNumber(), request.getPageSize()));
    return pages;
  }


}
