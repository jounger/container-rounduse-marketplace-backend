package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierRequest extends SignUpRequest{

	private String website;
	
	private String companyName;

	private String shortName;
	
	private String description;
	
	private String tin;
	
	private String fax;
}
