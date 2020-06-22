package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IcdRequest {
  
    private Long id;
	
	@NotBlank
	private String fullname;
	
	@NotBlank
	private String nameCode;
	
	@NotBlank
	private String address;
}
