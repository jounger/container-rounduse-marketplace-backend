package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.User;
import com.crm.models.dto.UserDto;

public class UserMapper {

  public static UserDto toUserDto(User user) {
    if (user == null) {
      return null;
    }

    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setUsername(user.getUsername());
    userDto.setEmail(user.getEmail());
    userDto.setPhone(user.getPhone());
    userDto.setStatus(user.getStatus());
    userDto.setProfileImagePath(user.getProfileImagePath());

    Set<String> roles = new HashSet<>();
    user.getRoles().forEach(role -> roles.add(role.getName()));
    userDto.setRoles(roles);
    return userDto;
  }
}
