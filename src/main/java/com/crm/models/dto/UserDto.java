package com.crm.models.dto;

import java.util.HashSet;
import java.util.Set;

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

  private String phone;

  private String fullname;

  private String status;

  private Set<String> roles = new HashSet<>();

  private String address;

  private String profileImagePath;

}
