package com.crm.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.crm.common.ErrorMessage;
import com.crm.enums.EnumUserStatus;
import com.crm.exception.ForbiddenException;
import com.crm.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String username;

  private String fullname;

  @JsonIgnore
  private String password;

  private String email;

  private String phone;

  private String address;

  private String status;

  private String profileImagePath;

  private Collection<? extends GrantedAuthority> authorities;

  public static UserDetailsImpl build(User user) {

    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

    UserDetailsImpl userDetailsImpl = new UserDetailsImpl();
    userDetailsImpl.setId(user.getId());
    userDetailsImpl.setUsername(user.getUsername());
    userDetailsImpl.setFullname(user.getFullname());
    userDetailsImpl.setPassword(user.getPassword());
    userDetailsImpl.setEmail(user.getEmail());
    userDetailsImpl.setPhone(user.getPhone());
    userDetailsImpl.setAddress(user.getAddress());
    userDetailsImpl.setStatus(user.getStatus());
    userDetailsImpl.setProfileImagePath(user.getProfileImagePath());
    userDetailsImpl.setAuthorities(authorities);
    return userDetailsImpl;
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
    if (this.status.equals(EnumUserStatus.BANNED.name())) {
      throw new ForbiddenException(ErrorMessage.BANNED_ACCOUNT);
      //return false;
    }
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
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl userDetails = (UserDetailsImpl) o;
    return Objects.equals(id, userDetails.id);
  }

}
