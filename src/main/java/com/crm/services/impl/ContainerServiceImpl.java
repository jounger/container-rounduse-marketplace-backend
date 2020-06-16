package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Container;
import com.crm.models.ContainerType;
import com.crm.models.Driver;
import com.crm.models.ShippingLine;
import com.crm.payload.request.ContainerRequest;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.DriverRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.services.ContainerService;

@Service
public class ContainerServiceImpl implements ContainerService{

	@Autowired
	private ContainerRepository containerRepository;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private ShippingLineRepository shippingLineRepository;
	
	@Autowired
	private ContainerTypeRepository containerTypeRepository;
	
	@Override
	public void saveContainer(ContainerRequest request) {
		if(containerRepository.existsByContainerNumber(request.getContainerNumber())) {
			throw new DuplicateRecordException("ERROR: Container already exists");
		}
		Container container = new Container();
		Driver driver = driverRepository.findByUsername(request.getDriverUsername())
				.orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
		ShippingLine shippingLine = shippingLineRepository.findByName(request.getShippingLineName())
				.orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
		container.setShippingLine(shippingLine);
		ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
				.orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
		container.setContainerType(containerType);
		container.setStatus(request.getStatus());
		container.setDriver(driver);
		container.setContainerTrailer(request.getContainerTrailer());
		container.setContainerTractor(request.getContainerTractor());
		container.setContainerNumber(request.getContainerNumber());
		container.setBlNumber(request.getBLNumber());
		container.setLicensePlate(request.getLicensePlate());
		container.setEmptyTime(request.getEmptyTime());
		container.setReturnStation(request.getReturnStation());
		container.setFeeDET(request.getFeeDET());
		containerRepository.save(container);
	}

}
