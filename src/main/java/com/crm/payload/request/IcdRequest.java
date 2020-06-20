package com.crm.payload.request;

import java.util.Collection;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IcdRequest {
  
    private Long id;
	
	@NotBlank
	private String fullname;
	
	@NotBlank
	@JsonProperty("name_code")
	private String nameCode;
	
	@NotBlank
	private String address;
	
	@NotBlank
	@JsonProperty("shipping_lines")
	private Collection<String> shippingLines;
}
