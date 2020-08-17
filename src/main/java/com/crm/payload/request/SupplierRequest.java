package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SupplierRequest extends SignUpRequest {

  @NotBlank
  @Size(min = 5, max = 50)
  @NonNull
  private String website;

  @NotBlank
  @Size(min = 5, max = 100)
  private String companyName;

  @NotBlank
  @Size(min = 2, max = 10)
  private String companyCode;

  @NotBlank
  @Size(min = 5, max = 200)
  private String companyDescription;

  @NotBlank
  @Size(min = 5, max = 200)
  private String companyAddress;

  @NotBlank
  @Size(min = 5, max = 20)
  private String tin;

  @NotBlank
  @NotEmpty
  @Size(min = 5, max = 20)
  private String fax;

  private Integer ratingValue;
}
