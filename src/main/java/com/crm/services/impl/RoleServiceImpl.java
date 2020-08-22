package com.crm.services.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;
import com.crm.repository.PermissionRepository;
import com.crm.repository.RoleRepository;
import com.crm.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @Override
  public Role createRole(RoleRequest request) {
    Role role = new Role();
    if (roleRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException(ErrorMessage.ROLE_ALREADY_EXISTS);
    }
    role.setName(request.getName());
    role.setDescription(request.getDescription());
    List<String> permissionsString = request.getPermissions();
    permissionsString.forEach(permission -> {
      Permission rolePermission = permissionRepository.findByName(permission)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.PERMISSION_NOT_FOUND));
      role.getPermissions().add(rolePermission);
    });

    Role _role = roleRepository.save(role);
    return _role;
  }

  @Override
  public Page<Role> getRoles(PaginationRequest request) {
    Page<Role> pages = roleRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return pages;
  }

  @Override
  public void removeRole(Long id) {
    Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));
    roleRepository.delete(role);
  }

  @Override
  public Role updateRole(RoleRequest request) {
    Role role = roleRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.ROLE_NOT_FOUND));

    role.setPermissions(new HashSet<>());
    List<String> permissionsString = request.getPermissions();
    permissionsString.forEach(permission -> {
      Permission rolePermission = permissionRepository.findByName(permission)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.PERMISSION_NOT_FOUND));
      role.getPermissions().add(rolePermission);
    });

    role.setName(request.getName());
    role.setDescription(request.getDescription());

    Role _role = roleRepository.save(role);
    return _role;
  }

}
