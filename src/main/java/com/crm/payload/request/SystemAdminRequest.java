package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemAdminRequest extends SignUpRequest{
	
	private String name;
	
	private Boolean rootUser;
}
