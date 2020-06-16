package com.crm.payload.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingLineRequest extends SignUpRequest{

	private String website;
	
	private String name;
	
	@JsonProperty("short_name")
	private String shortName;
	
	@JsonProperty("icd_name_list")
	private Set<String> icdNameList;
	
}
