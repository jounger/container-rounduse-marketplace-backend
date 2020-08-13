package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

  @NotBlank
  @Size(min = 6, max = 120)
  private String oldPassword;

  @NotBlank(message = "Password must be not blank")
  @Size(min = 6, max = 120, message = "Password is not valid")
  private String newPassword;

}
