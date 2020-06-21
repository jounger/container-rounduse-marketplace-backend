package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Port;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PortRequest;

public interface PortService {

	void savePort(PortRequest request);
	
	void updatePort(PortRequest request);
    
    void deletePort(Long id);
    
    Page<Port> getPorts(PaginationRequest request);
    
    Port getPortById(Long id);
}
