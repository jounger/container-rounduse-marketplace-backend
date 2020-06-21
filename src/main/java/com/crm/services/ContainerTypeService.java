package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerType;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerTypeService {

	void saveContainerType(ContainerTypeRequest request);
	
	void updateContainerType(ContainerTypeRequest request);
    
    void deleteContainerType(Long id);
    
    Page<ContainerType> getContainerTypes(PaginationRequest request);
    
    ContainerType getContainerTypeById(Long id);
}
