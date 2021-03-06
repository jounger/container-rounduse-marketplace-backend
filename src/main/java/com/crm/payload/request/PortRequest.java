package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PortRequest {

  private Long id;

  @NotBlank
  private String fullname;

  @NotBlank
  private String nameCode;

  private String address;

}
