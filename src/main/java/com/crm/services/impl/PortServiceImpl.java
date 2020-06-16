package com.crm.services.impl;

import com.crm.repository.PortRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.models.Port;
import com.crm.payload.request.PortRequest;
import com.crm.services.PortService;

@Service
public class PortServiceImpl implements PortService{

	@Autowired
	private PortRepository portRepository;
	
	@Override
	public void savePort(PortRequest request) {
		if(portRepository.existsByName(request.getName())) {
			throw new DuplicateRecordException("ERROR: Port already exists.");
		}
		Port port = new Port();
		port.setName(request.getName());
		port.setNameCode(request.getNameCode());
		port.setAddress(request.getAddress());
		portRepository.save(port);
		
	}

}
