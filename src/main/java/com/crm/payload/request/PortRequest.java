package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortRequest {
  
    private Long id;
	
	private String fullname;

	private String nameCode;
	
	private String address;
	
}
