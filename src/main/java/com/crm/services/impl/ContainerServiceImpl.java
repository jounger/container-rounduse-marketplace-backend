package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Bid;
import com.crm.models.Container;
import com.crm.models.ContainerType;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.AddressRepository;
import com.crm.repository.BidRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.DriverRepository;
import com.crm.repository.ForwarderRepository;
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
  private ForwarderRepository forwarderRepository;

  @Autowired
  private ShippingLineRepository shippingLineRepository;

  @Autowired
  private ContainerTypeRepository containerTypeRepository;

  @Autowired
  private PortRepository portRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Override
  public void createContainer(ContainerRequest request) {
    Container container = new Container();
    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    container.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    container.setContainerType(containerType);

    container.setStatus(EnumSupplyStatus.findByName(request.getStatus()));

    Driver driver = driverRepository.findByUsername(request.getDriver())
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    container.setDriver(driver);

    Forwarder forwarder = forwarderRepository.findByUsername(request.getForwarder())
        .orElseThrow(() -> new NotFoundException("ERROR: Forwarder is not found."));
    container.setForwarder(forwarder);

    container.setContainerTrailer(request.getContainerTrailer());
    container.setContainerTractor(request.getContainerTractor());
    container.setContainerNumber(request.getContainerNumber());
    container.setBlNumber(request.getBlNumber());
    container.setLicensePlate(request.getLicensePlate());

    LocalDateTime emptyTime = Tool.convertToLocalDateTime(request.getEmptyTime());
    container.setEmptyTime(emptyTime);

    LocalDateTime pickUpTime = Tool.convertToLocalDateTime(request.getPickUpTime());
    container.setPickUpTime(pickUpTime);

    Address returnStation = (Address) request.getReturnStation();
    container.setReturnStation(returnStation);

    Port port = portRepository.findByNameCode(request.getPortOfDelivery())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    container.setPortOfDelivery(port);

    Set<Long> bids = request.getBids();
    Set<Bid> listbids = new HashSet<>();

    if (bids != null) {
      bids.forEach(item -> {
        Bid bid = bidRepository.findById(item).orElseThrow(() -> new NotFoundException("Error: Bid is not found"));
        listbids.add(bid);
      });
    }
    container.setBids(listbids);

    container.setFreeTime(request.getFreeTime());

    containerRepository.save(container);
  }

  @Override
  public Page<Container> getContainers(PaginationRequest request) {
    Page<Container> pages = containerRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public Container updateContainer(ContainerRequest request) {
    Container container = containerRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    
    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    container.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    container.setContainerType(containerType);

    container.setStatus(EnumSupplyStatus.findByName(request.getStatus()));

    Driver driver = driverRepository.findByUsername(request.getDriver())
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    container.setDriver(driver);

    Forwarder forwarder = forwarderRepository.findByUsername(request.getForwarder())
        .orElseThrow(() -> new NotFoundException("ERROR: Forwarder is not found."));
    container.setForwarder(forwarder);

    container.setContainerTrailer(request.getContainerTrailer());
    container.setContainerTractor(request.getContainerTractor());
    container.setContainerNumber(request.getContainerNumber());
    container.setBlNumber(request.getBlNumber());
    container.setLicensePlate(request.getLicensePlate());

    LocalDateTime emptyTime = Tool.convertToLocalDateTime(request.getEmptyTime());
    container.setEmptyTime(emptyTime);

    LocalDateTime pickUpTime = Tool.convertToLocalDateTime(request.getPickUpTime());
    container.setPickUpTime(pickUpTime);

    Address returnStationReq = (Address) request.getReturnStation();
    Address returnStation = addressRepository.findById(container.getReturnStation().getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Address is not found."));
    returnStation.setCity(returnStationReq.getCity());
    returnStation.setStreet(returnStationReq.getStreet());
    returnStation.setCounty(returnStationReq.getCounty());
    returnStation.setCountry(returnStationReq.getCountry());
    returnStation.setPostalCode(returnStationReq.getPostalCode());
//    container.setReturnStation(returnStation);

    Port port = portRepository.findByNameCode(request.getPortOfDelivery())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    container.setPortOfDelivery(port);

    Set<Long> bids = request.getBids();
    Set<Bid> listbids = new HashSet<>();

    if (bids != null) {
      bids.forEach(item -> {
        Bid bid = bidRepository.findById(item).orElseThrow(() -> new NotFoundException("Error: Bid is not found"));
        listbids.add(bid);
      });
    }
    container.setBids(listbids);

    container.setFreeTime(request.getFreeTime());

    containerRepository.save(container);

    return container;
  }

  @Override
  public void removeContainer(Long id) {
    if (containerRepository.existsById(id)) {
      containerRepository.deleteById(id);
    } else {
      throw new NotFoundException("ERROR: Container is not found.");
    }
  }

  @Override
  public Container getContainerById(Long id) {
    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    return container;
  }

  @Override
  public Page<Container> getContainersByForwarder(Long id, PaginationRequest request) {
    Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());
    Page<Container> pages = containerRepository.findByForwarderId(id, pageable);
    return pages;
  }

}
