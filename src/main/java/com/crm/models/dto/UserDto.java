package com.crm.models.dto;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

  private Long id;
  
  private String username;
  
  private String email;
  
  private String fullname;
  
  private Set<Role> roles = new HashSet<>();
  
}
