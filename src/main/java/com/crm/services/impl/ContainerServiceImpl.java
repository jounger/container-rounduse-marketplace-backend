package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Container;
import com.crm.models.ContainerType;
import com.crm.models.Driver;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.DriverRepository;
import com.crm.repository.PortRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.services.ContainerService;

@Service
public class ContainerServiceImpl implements ContainerService {

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private ShippingLineRepository shippingLineRepository;

  @Autowired
  private ContainerTypeRepository containerTypeRepository;

  @Autowired
  private PortRepository portRepository;

  @Override
  public void saveContainer(ContainerRequest request) {
    Container container = new Container();
    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLineName())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    container.setShippingLine(shippingLine);
    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    container.setContainerType(containerType);
    container.setStatus(EnumSupplyStatus.findByName(request.getStatus()));
    Driver driver = driverRepository.findByUsername(request.getDriverUsername())
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    container.setDriver(driver);
    container.setContainerTrailer(request.getContainerTrailer());
    container.setContainerTractor(request.getContainerTractor());
    container.setContainerNumber(request.getContainerNumber());
    container.setBlNumber(request.getBLNumber());
    container.setLicensePlate(request.getLicensePlate());
    container.setEmptyTime(Tool.convertToLocalDateTime(request.getEmptyTime()));
    container.setPickUpTime(Tool.convertToLocalDateTime(request.getPickUpTime()));

    Address returnStation = (Address) request.getReturnStation();
    if (returnStation == null) {
      throw new NotFoundException("Error: ReturnStation is not found");
    } else {
      container.setReturnStation(returnStation);
    }

    Port port = portRepository.findByNameCode(request.getPortName())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    container.setPortOfDelivery(port);

    container.setFreeTime(request.getFeeTime());

    containerRepository.save(container);
  }

  @Override
  public Page<Container> getContainers(PaginationRequest request) {
    Page<Container> pages = containerRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public void editContainer(Long id, ContainerRequest request) {
    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLineName())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    container.setShippingLine(shippingLine);
    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    container.setContainerType(containerType);
    container.setStatus(EnumSupplyStatus.findByName(request.getStatus()));
    Driver driver = driverRepository.findByUsername(request.getDriverUsername())
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    container.setDriver(driver);
    container.setContainerTrailer(request.getContainerTrailer());
    container.setContainerTractor(request.getContainerTractor());
    container.setContainerNumber(request.getContainerNumber());
    container.setBlNumber(request.getBLNumber());
    container.setLicensePlate(request.getLicensePlate());
    container.setEmptyTime(Tool.convertToLocalDateTime(request.getEmptyTime()));
    container.setPickUpTime(Tool.convertToLocalDateTime(request.getPickUpTime()));

    Address returnStation = (Address) request.getReturnStation();
    if (returnStation == null) {
      throw new NotFoundException("Error: ReturnStation is not found");
    } else {
      container.setReturnStation(returnStation);
    }

    Port port = portRepository.findByNameCode(request.getPortName())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    container.setPortOfDelivery(port);

    container.setFreeTime(request.getFeeTime());

    containerRepository.save(container);
  }

  @Override
  public void deleteContainer(Long id) {
    containerRepository.deleteById(id);

  }

  @Override
  public Container getContainerById(Long id) {
    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    return container;
  }

}
