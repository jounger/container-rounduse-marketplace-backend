package com.crm.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortRequest {
  
    private Long id;
	
	private String fullname;
	
	@JsonProperty("name_code")
	private String nameCode;
	
	private String address;
}
