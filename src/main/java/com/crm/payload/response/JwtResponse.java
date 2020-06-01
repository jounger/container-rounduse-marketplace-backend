package com.crm.payload.response;

import com.crm.models.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

  @JsonProperty("id_token")
  private String idToken;
  
  @JsonProperty("userInfo")
  private UserDto userInfo;
}
