package com.crm.services.impl;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Container;
import com.crm.models.ContainerTractor;
import com.crm.models.Forwarder;
import com.crm.payload.request.ContainerTractorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerTractorRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.VehicleRepository;
import com.crm.services.ContainerTractorService;

@Service
public class ContainerTractorServiceImpl implements ContainerTractorService {

  @Autowired
  ContainerTractorRepository containerTractorRepository;

  @Autowired
  ForwarderRepository forwarderRepository;

  @Autowired
  VehicleRepository vehicleRepository;

  @Override
  public ContainerTractor getContainerTractorById(Long id) {
    ContainerTractor containerTractor = containerTractorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: containerTractor is not found."));
    return containerTractor;
  }

  @Override
  public Page<ContainerTractor> getContainerTractors(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ContainerTractor> pages = containerTractorRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public ContainerTractor createContainerTractor(Long id, ContainerTractorRequest request) {

    ContainerTractor containerTractor = new ContainerTractor();

    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Forwarder is not found."));

    if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
      throw new DuplicateRecordException("Error: LicensePlate has been existed");
    } else {
      containerTractor.setLicensePlate(request.getLicensePlate());
    }

    containerTractor.setForwarder(forwarder);

    containerTractorRepository.save(containerTractor);
    return containerTractor;
  }

  @Override
  public ContainerTractor updateContainerTractor(Long userId, ContainerTractorRequest request) {

    if (forwarderRepository.existsById(userId)) {

      ContainerTractor containerTractor = containerTractorRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));

      if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())
          && !request.getLicensePlate().equals(containerTractor.getLicensePlate())) {
        throw new DuplicateRecordException("Error: LicensePlate has been existed");
      } else {
        containerTractor.setLicensePlate(request.getLicensePlate());
      }

      containerTractorRepository.save(containerTractor);
      return containerTractor;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public ContainerTractor editContainerTractor(Map<String, Object> updates, Long id, Long userId) {
    if (forwarderRepository.existsById(userId)) {

      ContainerTractor containerTractor = containerTractorRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));

      String licensePlate = (String) updates.get("licensePlate");
      if (licensePlate != null && !licensePlate.isEmpty() && !licensePlate.equals(containerTractor.getLicensePlate())) {
        if (vehicleRepository.existsByLicensePlate(licensePlate)) {
          throw new DuplicateRecordException("Error: LicensePlate has been existed");
        } else {
          containerTractor.setLicensePlate(licensePlate);
        }
      }

      containerTractorRepository.save(containerTractor);
      return containerTractor;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public void removeContainerTractor(Long id, Long userId) {

    if (forwarderRepository.existsById(userId)) {
      ContainerTractor containerTractor = containerTractorRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));

      Collection<Container> containers = containerTractor.getContainers();
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(
              String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
        }
      });
      containerTractorRepository.delete(containerTractor);
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }

  }

}
