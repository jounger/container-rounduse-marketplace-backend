package com.crm.models.mapper;

import com.crm.models.Token;
import com.crm.models.dto.TokenDto;

public class TokenMapper {

  public static TokenDto toTokenDto(Token token) {
    TokenDto tokenDto = new TokenDto();
    tokenDto.setToken(token.getToken());
    return tokenDto;
  }
}
