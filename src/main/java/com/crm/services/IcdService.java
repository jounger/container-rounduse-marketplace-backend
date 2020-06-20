package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Icd;
import com.crm.payload.request.IcdRequest;
import com.crm.payload.request.PaginationRequest;

public interface IcdService {
	
	void saveIcd(IcdRequest request);
	
	void editIcd(IcdRequest request);
	  
	void deleteIcd(Long id);
	
	Page<Icd> getIcds(PaginationRequest request);
	
	Icd getIcdById(Long id);
}
