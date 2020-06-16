package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AddressRequest {

	@NotBlank
	@Size(min = 2, max = 50)
	private String address;

	@NotBlank
	@Size(min = 2, max = 50)
	private String city;

	@NotBlank
	@Size(min = 2, max = 50)
	private String country;

	@NotBlank
	@Size(min = 6, max = 6)
	private String postalCode;
}
