package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.models.ContainerType;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.repository.ContainerTypeRepository;
import com.crm.services.ContainerTypeService;

@Service
public class ContainerTypeServiceImpl implements ContainerTypeService{

	@Autowired
	private ContainerTypeRepository containerTypeRepository;

	@Override
	public void saveContainerType(ContainerTypeRequest request) {
		if(containerTypeRepository.existsByName(request.getName())) {
			throw new DuplicateRecordException("ERROR: Type name already exists.");
		}
		try {
			ContainerType containerType = new ContainerType();
			containerType.setName(request.getName());
			containerType.setDescription(request.getDescription());
			containerType.setTareWeight(Float.parseFloat(request.getTareWeight()));
			containerType.setPayloadCapacity(Float.parseFloat(request.getPayloadCapacity()));
			containerType.setCubicCapacity(Float.parseFloat(request.getCubicCapacity()));
			containerType.setInternalLength(Float.parseFloat(request.getInternalLength()));
			containerType.setInternalHeight(Float.parseFloat(request.getInternalHeight()));
			containerType.setInternalWeight(Float.parseFloat(request.getInternalWeight()));
			containerType.setDoorOpeningHeight(Float.parseFloat(request.getDoorOpeningHeight()));
			containerType.setDoorOpeningWidth(Float.parseFloat(request.getDoorOpeningWidth()));
			containerTypeRepository.save(containerType);
		} catch (Exception e) {
			throw new InternalException("ERROR: Parameter must be float");
		}
		
	}

}
