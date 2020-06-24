package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequest extends SignUpRequest{

	private String fullname;
	
	private String driverLicense;
	
}
