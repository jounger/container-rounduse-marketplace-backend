package com.crm.services.impl;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumSupplyStatus;
import com.crm.enums.EnumTrailerType;
import com.crm.enums.EnumUnit;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.Forwarder;
import com.crm.payload.request.ContainerSemiTrailerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.VehicleRepository;
import com.crm.services.ContainerSemiTrailerService;

@Service
public class ContainerSemiTrailerServiceImpl implements ContainerSemiTrailerService {

  @Autowired
  ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Autowired
  VehicleRepository vehicleRepository;

  @Autowired
  ForwarderRepository forwarderRepository;

  @Override
  public ContainerSemiTrailer getContainerSemiTrailerById(Long id) {
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));
    return containerSemiTrailer;
  }

  @Override
  public Page<ContainerSemiTrailer> getContainerSemiTrailers(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ContainerSemiTrailer> pages = containerSemiTrailerRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public ContainerSemiTrailer createContainerSemiTrailer(Long id, ContainerSemiTrailerRequest request) {

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();

    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Forwarder is not found."));

    if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
      throw new DuplicateRecordException("Error: LicensePlate has been existed");
    } else {
      containerSemiTrailer.setLicensePlate(request.getLicensePlate());
    }

    containerSemiTrailer.setForwarder(forwarder);

    try {
      containerSemiTrailer.setType(EnumTrailerType.findByName(request.getType()).name());
    } catch (Exception e) {
      throw new NotFoundException("ERROR: Type is not found.");
    }

    try {
      containerSemiTrailer.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    } catch (Exception e) {
      throw new NotFoundException("ERROR: UnitOfMeasurement is not found.");
    }

    containerSemiTrailerRepository.save(containerSemiTrailer);
    return containerSemiTrailer;
  }

  @Override
  public ContainerSemiTrailer updateContainerSemiTrailer(Long id, ContainerSemiTrailerRequest request) {

    if (forwarderRepository.existsById(id)) {

      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));

      if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())
          && !request.getLicensePlate().equals(containerSemiTrailer.getLicensePlate())) {
        throw new DuplicateRecordException("Error: LicensePlate has been existed");
      } else {
        containerSemiTrailer.setLicensePlate(request.getLicensePlate());
      }

      try {
        containerSemiTrailer.setType(EnumTrailerType.findByName(request.getType()).name());
      } catch (Exception e) {
        throw new NotFoundException("ERROR: Type is not found.");
      }

      try {
        containerSemiTrailer.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
      } catch (Exception e) {
        throw new NotFoundException("ERROR: UnitOfMeasurement is not found.");
      }

      containerSemiTrailerRepository.save(containerSemiTrailer);
      return containerSemiTrailer;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public ContainerSemiTrailer editContainerSemiTrailer(Map<String, Object> updates, Long id, Long userId) {

    if (forwarderRepository.existsById(userId)) {

      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));

      String licensePlate = (String) updates.get("licensePlate");
      if (licensePlate != null && !licensePlate.isEmpty()
          && !licensePlate.equals(containerSemiTrailer.getLicensePlate())) {
        if (vehicleRepository.existsByLicensePlate(licensePlate)) {
          throw new DuplicateRecordException("Error: LicensePlate has been existed");
        } else {
          containerSemiTrailer.setLicensePlate(licensePlate);
        }
      }

      String type = (String) updates.get("type");
      if (type != null && !type.isEmpty() && !type.equals(containerSemiTrailer.getType())) {
        try {
          containerSemiTrailer.setType(EnumTrailerType.findByName(type).name());
        } catch (Exception e) {
          throw new NotFoundException("ERROR: Type is not found.");
        }
      }

      String unitOfMeasurement = (String) updates.get("unitOfMeasurement");
      if (unitOfMeasurement != null && !unitOfMeasurement.isEmpty()
          && !unitOfMeasurement.equals(containerSemiTrailer.getUnitOfMeasurement())) {
        try {
          containerSemiTrailer.setUnitOfMeasurement(EnumUnit.findByName(unitOfMeasurement).name());
        } catch (Exception e) {
          throw new NotFoundException("ERROR: UnitOfMeasurement is not found.");
        }
      }
      containerSemiTrailerRepository.save(containerSemiTrailer);
      return containerSemiTrailer;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public void removeContainerSemiTrailer(Long id, Long userId) {

    if (forwarderRepository.existsById(userId)) {
      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));

      Collection<Container> containers = containerSemiTrailer.getContainers();
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(
              String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
        }
      });
      containerSemiTrailerRepository.delete(containerSemiTrailer);

    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

}
