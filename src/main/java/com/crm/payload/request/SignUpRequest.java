package com.crm.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.crm.models.Address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
  
  private Long id;
  
  @NotBlank
  @Size(min = 2, max = 20)
  private String username;
  
  @NotBlank
  @Size(min = 10, max = 10)
  private String phone;

  @NotBlank
  @Size(min=5, max = 50)
  @Email
  private String email;
  
  private Set<String> roles;
  
  private String status;
  
  private Address address;
  
  @NotBlank
  @Size(min = 6, max = 120)
  private String password;
}
