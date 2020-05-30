package com.crm.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumRole;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SignUpRequest;
import com.crm.repository.RoleRepository;
import com.crm.repository.UserRepository;
import com.crm.services.UserService;

@Service
public class UserServiceImpl implements UserService {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private RoleRepository roleRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void saveUser(SignUpRequest request) {
    if(userRepository.existsByUsername(request.getUsername()) || 
        userRepository.existsByEmail(request.getEmail())) {
      throw new DuplicateRecordException("Error: User has been existed");
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setFullname(request.getFullname());
    
    Set<String> rolesString = request.getRoles();
    Set<Role> roles = new HashSet<>();
    List<EnumRole> rolesEnum = Arrays.asList(EnumRole.values());
    
    if(rolesString == null) {
      Role userRole = roleRepository.findByName(EnumRole.ROLE_USER)
          .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
      roles.add(userRole);
    } else {
      rolesString.forEach(role -> {
        for(int i = 0; i < rolesEnum.size(); i++) {
          if(role.equalsIgnoreCase(rolesEnum.get(i).name().split("_")[1])) {
            Role userRole = roleRepository.findByName(rolesEnum.get(i))
                .orElseThrow(() -> new NotFoundException("Error: Role is not found"));
            roles.add(userRole);
          }
        }
      });
    }
    user.setRoles(roles);
    String encoder = passwordEncoder.encode(request.getPassword());
    user.setPassword(encoder);
    userRepository.save(user);
  }

  @Override
  public Page<User> getUsers(PaginationRequest request) {
    Page<User> pages = userRepository.findAll(PageRequest.of(request.getPageNumber(), request.getPageSize()));
    return pages;
  }

}
