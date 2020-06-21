package com.crm.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortDto {

  private Long id;
  
  private String fullname;

  @JsonProperty("name_code")
  private String nameCode;

  private String address;
  
}
