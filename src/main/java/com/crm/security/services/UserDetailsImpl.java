package com.crm.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.crm.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;
  
  private Long id;
  private String username;
  
  @JsonIgnore
  private String password;
  private String email;
  private String fullname;
  
  private Collection<? extends GrantedAuthority> authorities;
  
  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles()
        .stream().map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());
    return new UserDetailsImpl(user.getId(), 
        user.getUsername(), 
        user.getPassword(), 
        user.getEmail(), 
        user.getFullname(), 
        authorities);
    
  }
  
  public Long getId() {
    return id;
  }
  
  public String getEmail() {
    return email;
  }
  
  public String getFullname() {
    return fullname;
  }
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // TODO Auto-generated method stub
    return authorities;
  }

  @Override
  public String getPassword() {
    // TODO Auto-generated method stub
    return password;
  }

  @Override
  public String getUsername() {
    // TODO Auto-generated method stub
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean isEnabled() {
    // TODO Auto-generated method stub
    return true;
  }
  
  @Override
  public boolean equals(Object o) {
    if(this == o)
      return true;
    if(o == null || getClass() != o.getClass()) 
      return false;
    UserDetailsImpl userDetails = (UserDetailsImpl) o;
    return Objects.equals(id, userDetails.id);
  }

}
