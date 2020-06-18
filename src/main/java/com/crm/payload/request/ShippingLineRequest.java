package com.crm.payload.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingLineRequest extends SignUpRequest{

	private String website;
	
	@JsonProperty("company_name")
	private String companyName;
	
	@JsonProperty("company_code")
	private String companyCode;
	
	@JsonProperty("icds_name")
	private Set<String> icdsName;
	
}
