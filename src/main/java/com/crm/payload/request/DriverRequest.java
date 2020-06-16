package com.crm.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequest extends SignUpRequest{

	private String name;
	
	private String license;
	
	@JsonProperty("forwarder_username")
	private String forwarderUsername;
}
