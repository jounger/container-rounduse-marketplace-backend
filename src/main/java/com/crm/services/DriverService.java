package com.crm.services;

import com.crm.payload.request.DriverRequest;

public interface DriverService {

	void saveDriver(DriverRequest request);
	
	void updateDriver(DriverRequest request);
	
	void deleteDriver(String username);
}
