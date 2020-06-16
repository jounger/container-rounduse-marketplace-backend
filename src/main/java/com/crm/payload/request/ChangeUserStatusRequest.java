package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserStatusRequest {

	@NotBlank
	private String username;
	
	@NotBlank
	private String status;
	
//	private HashMap<String, String> reqestProperties;
}
