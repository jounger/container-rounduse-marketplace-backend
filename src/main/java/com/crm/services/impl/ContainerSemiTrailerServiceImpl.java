package com.crm.services.impl;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
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
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.VehicleRepository;
import com.crm.services.ContainerSemiTrailerService;
import com.crm.specification.builder.ContainerSemiTrailerSpecificationsBuilder;

@Service
public class ContainerSemiTrailerServiceImpl implements ContainerSemiTrailerService {

  @Autowired
  ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Autowired
  VehicleRepository vehicleRepository;

  @Autowired
  ForwarderRepository forwarderRepository;

  @Autowired
  ContainerRepository containerRepository;

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
  public ContainerSemiTrailer createContainerSemiTrailer(Long userId, ContainerSemiTrailerRequest request) {

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();

    Forwarder forwarder = forwarderRepository.findById(userId)
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

    containerSemiTrailer.setNumberOfAxles(request.getNumberOfAxles());

    containerSemiTrailerRepository.save(containerSemiTrailer);
    return containerSemiTrailer;
  }

  @Override
  public ContainerSemiTrailer updateContainerSemiTrailer(Long userId, ContainerSemiTrailerRequest request) {

    if (forwarderRepository.existsById(userId)) {

      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));

      if (!containerSemiTrailer.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned containerSemiTrailer", userId));
      }

      Collection<Container> containers = containerRepository.findByTrailer(request.getId(),
          EnumSupplyStatus.COMBINED.name(), EnumSupplyStatus.BIDDING.name());
      if (containers != null) {
        containers.forEach(item -> {
          if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
              || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
            throw new InternalException(
                String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
          }
        });
      }

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

      containerSemiTrailer.setNumberOfAxles(request.getNumberOfAxles());

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

      if (!containerSemiTrailer.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned containerSemiTrailer", userId));
      }

      Collection<Container> containers = containerRepository.findByTrailer(id, EnumSupplyStatus.COMBINED.name(),
          EnumSupplyStatus.BIDDING.name());
      if (containers != null) {
        containers.forEach(item -> {
          if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
              || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
            throw new InternalException(
                String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
          }
        });
      }

      String licensePlate = String.valueOf(updates.get("licensePlate"));
      if (licensePlate != null && !licensePlate.isEmpty()
          && !licensePlate.equals(containerSemiTrailer.getLicensePlate())) {
        if (vehicleRepository.existsByLicensePlate(licensePlate)) {
          throw new DuplicateRecordException("Error: LicensePlate has been existed");
        } else {
          containerSemiTrailer.setLicensePlate(licensePlate);
        }
      }

      String numberOfAxles = String.valueOf(updates.get("numberOfAxles"));
      if (numberOfAxles != null && !numberOfAxles.isEmpty()) {
        containerSemiTrailer.setNumberOfAxles(Integer.valueOf(numberOfAxles));
      }

      String type = String.valueOf(updates.get("type"));
      if (type != null && !type.isEmpty() && !type.equals(containerSemiTrailer.getType())) {
        try {
          containerSemiTrailer.setType(EnumTrailerType.findByName(type).name());
        } catch (Exception e) {
          throw new NotFoundException("ERROR: Type is not found.");
        }
      }

      String unitOfMeasurement = String.valueOf(updates.get("unitOfMeasurement"));
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

      if (!containerSemiTrailer.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned containerSemiTrailer", userId));
      }

      Collection<Container> containers = containerRepository.findByTrailer(id, EnumSupplyStatus.COMBINED.name(),
          EnumSupplyStatus.BIDDING.name());
      if (containers != null) {
        containers.forEach(item -> {
          if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
              || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
            throw new InternalException(
                String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
          }
        });
      }
      containerSemiTrailerRepository.delete(containerSemiTrailer);

    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Page<ContainerSemiTrailer> getContainerSemiTrailersByForwarder(Long userId, PaginationRequest request) {
    if (forwarderRepository.existsById(userId)) {
      PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
          Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<ContainerSemiTrailer> pages = containerSemiTrailerRepository.findByForwarder(userId, pageRequest);
      return pages;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Page<ContainerSemiTrailer> searchContainerSemiTrailers(PaginationRequest request, String search) {
    ContainerSemiTrailerSpecificationsBuilder builder = new ContainerSemiTrailerSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<ContainerSemiTrailer> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<ContainerSemiTrailer> pages = containerSemiTrailerRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public ContainerSemiTrailer getContainerSemiTrailerByLicensePlate(String licensePlate) {
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(licensePlate)
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));
    return containerSemiTrailer;
  }

}
