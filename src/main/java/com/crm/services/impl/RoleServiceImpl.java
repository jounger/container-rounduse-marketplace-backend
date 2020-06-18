package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
    Role role = new Role();
    if (roleRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("Role already exists.");
    } else {
      role.setName(request.getName());
      roleRepository.save(role);
    }
  }

  @Override
  public Page<Role> getRoles(PaginationRequest request) {
    Page<Role> pages = roleRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public void deleteRole(long id) {
    Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role is not found"));
    roleRepository.delete(role);
  }

  @Override
  public void updateRole(long id, RoleRequest request) {
    Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role is not found"));
    role.setName(request.getName());
  }

}
