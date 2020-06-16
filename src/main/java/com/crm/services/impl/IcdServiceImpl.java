package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.models.Icd;
import com.crm.payload.request.IcdRequest;
import com.crm.repository.IcdRepository;
import com.crm.services.IcdService;

@Service
public class IcdServiceImpl implements IcdService{
	
	@Autowired
	private IcdRepository icdRepository;

	@Override
	public void saveIcd(IcdRequest request) {
		Icd icd = new Icd();
		icd.setName(request.getName());
		String nameCode = request.getNameCode();
		if(icdRepository.existsByNameCode(nameCode)) {
			throw new DuplicateRecordException("ICD name code already existed.");
		}
		icd.setNameCode(nameCode);
		icd.setAddress(request.getAddress());
		icdRepository.save(icd);
	}

}
