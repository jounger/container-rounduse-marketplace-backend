package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Container;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerService {
  
    Page<Container> getContainers(PaginationRequest request);
	
	void saveContainer(ContainerRequest request);
	
	void editContainer(ContainerRequest request);
	  
	void deleteContainer(Long id);
	  
	Container getContainerById(Long id);
	
	Page<Container> getContainersByMerchant(Long id, PaginationRequest request);
}
