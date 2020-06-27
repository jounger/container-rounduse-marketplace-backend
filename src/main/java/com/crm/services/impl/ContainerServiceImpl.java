package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.NotFoundException;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.Driver;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.DriverRepository;
import com.crm.services.ContainerService;

@Service
public class ContainerServiceImpl implements ContainerService {

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private BillOfLadingRepository billOfLadingRepository;

  @Override
  public Page<Container> getContainersByInbound(Long id, PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = containerRepository.getContainersByInbound(id, pageRequest);
    return pages;
  }

  @Override
  public Page<Container> getContainers(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = containerRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public Container getContainerById(Long id) {
    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    return container;
  }

  @Override
  public Page<Container> getContainersByBillOfLading(Long id, PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = containerRepository.getContainersByBillOfLading(id, pageRequest);
    return pages;
  }

  @Override
  public Container createContainer(Long id, ContainerRequest request) {
    Container container = new Container();
    BillOfLading billOfLading = billOfLadingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));

    container.setBillOfLading(billOfLading);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Driver driver = driverRepository.findByUsername(request.getDriver())
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    container.setDriver(driver);

    container.setContainerNumber(request.getContainerNumber());
    container.setTrailer(request.getTrailer());
    container.setTractor(request.getTractor());
    container.setLicensePlate(request.getLicensePlate());

    containerRepository.save(container);
    return container;
  }

  @Override
  public Container updateContainer(ContainerRequest request) {
    Container container = containerRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

    if (request.getStatus() != null) {
      container.setStatus(EnumSupplyStatus.findByName(request.getStatus()).name());
    }

    Driver driver = driverRepository.findByUsername(request.getDriver())
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    container.setDriver(driver);

    container.setTrailer(request.getTrailer());
    container.setTractor(request.getTractor());
    container.setContainerNumber(request.getContainerNumber());
    container.setLicensePlate(request.getLicensePlate());

    containerRepository.save(container);

    return container;
  }

  @Override
  public void removeContainer(Long id) {
    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())) {
      containerRepository.delete(container);
    }
  }

  @Override
  public Container editContainer(Map<String, Object> updates, Long id) {

    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

    String containerNumber = (String) updates.get("containerNumber");
    if (containerNumber != null) {
      container.setContainerNumber(containerNumber);
    }

    String driverRequest = (String) updates.get("driver");
    if (driverRequest != null) {
      Driver driver = driverRepository.findByUsername(driverRequest)
          .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
      container.setDriver(driver);
    }
    String trailer = (String) updates.get("trailer");
    if (trailer != null) {
      container.setTrailer(trailer);
    }

    String tractor = (String) updates.get("tractor");
    if (tractor != null) {
      container.setTractor(tractor);
    }

    String licensePlate = (String) updates.get("licensePlate");
    if (licensePlate != null) {
      container.setLicensePlate(licensePlate);
    }

    String status = (String) updates.get("status");
    if (status != null) {
      container.setStatus(status);
    }

    containerRepository.save(container);
    return container;
  }
}
