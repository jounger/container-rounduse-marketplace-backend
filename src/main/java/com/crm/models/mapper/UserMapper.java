package com.crm.models.mapper;

import com.crm.models.User;
import com.crm.models.dto.UserDto;

public class UserMapper {

  public static UserDto toUserDto(User user) {
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setUsername(user.getUsername());
    userDto.setEmail(user.getEmail());
    userDto.setFullname(user.getFullname());
    userDto.setRoles(user.getRoles());
    return userDto;
  }
}
