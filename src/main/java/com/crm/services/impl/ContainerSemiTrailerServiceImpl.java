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
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.enums.EnumTrailerType;
import com.crm.enums.EnumUnit;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));
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
  public ContainerSemiTrailer createContainerSemiTrailer(String username, ContainerSemiTrailerRequest request) {

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();

    Forwarder forwarder = forwarderRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND));

    if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
      throw new DuplicateRecordException(ErrorConstant.VEHICLE_LICENSE_PLATE_ALREADY_EXISTS);
    } else {
      containerSemiTrailer.setLicensePlate(request.getLicensePlate());
    }

    containerSemiTrailer.setForwarder(forwarder);

    try {
      containerSemiTrailer.setType(EnumTrailerType.findByName(request.getType()).name());
    } catch (Exception e) {
      throw new NotFoundException(ErrorConstant.TRAILER_TYPE_NOT_FOUND);
    }

    try {
      containerSemiTrailer.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    } catch (Exception e) {
      throw new NotFoundException(ErrorConstant.UNIT_OF_MEASUREMENT_NOT_FOUND);
    }

    containerSemiTrailer.setNumberOfAxles(request.getNumberOfAxles());

    ContainerSemiTrailer _containerSemiTrailer = containerSemiTrailerRepository.save(containerSemiTrailer);
    return _containerSemiTrailer;
  }

  @Override
  public ContainerSemiTrailer updateContainerSemiTrailer(String username, ContainerSemiTrailerRequest request) {

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));

    if (!containerSemiTrailer.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    Collection<Container> containers = containerRepository.findByTrailer(request.getId(),
        EnumSupplyStatus.COMBINED.name(), EnumSupplyStatus.BIDDING.name());
    if (containers != null) {
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorConstant.CONTAINER_BUSY);
        }
      });
    }

    if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())
        && !request.getLicensePlate().equals(containerSemiTrailer.getLicensePlate())) {
      throw new DuplicateRecordException(ErrorConstant.VEHICLE_LICENSE_PLATE_ALREADY_EXISTS);
    } else {
      containerSemiTrailer.setLicensePlate(request.getLicensePlate());
    }

    try {
      containerSemiTrailer.setType(EnumTrailerType.findByName(request.getType()).name());
    } catch (Exception e) {
      throw new NotFoundException(ErrorConstant.TRAILER_TYPE_NOT_FOUND);
    }

    try {
      containerSemiTrailer.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    } catch (Exception e) {
      throw new NotFoundException(ErrorConstant.UNIT_OF_MEASUREMENT_NOT_FOUND);
    }

    containerSemiTrailer.setNumberOfAxles(request.getNumberOfAxles());

    containerSemiTrailerRepository.save(containerSemiTrailer);
    return containerSemiTrailer;
  }

  @Override
  public ContainerSemiTrailer editContainerSemiTrailer(Map<String, Object> updates, Long id, String username) {

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));

    if (!containerSemiTrailer.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    Collection<Container> containers = containerRepository.findByTrailer(id, EnumSupplyStatus.COMBINED.name(),
        EnumSupplyStatus.BIDDING.name());
    if (containers != null) {
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorConstant.CONTAINER_BUSY);
        }
      });
    }

    String licensePlate = String.valueOf(updates.get("licensePlate"));
    if (updates.get("licensePlate") != null && !Tool.isEqual(containerSemiTrailer.getLicensePlate(), licensePlate)) {
      if (vehicleRepository.existsByLicensePlate(licensePlate)) {
        throw new DuplicateRecordException(ErrorConstant.VEHICLE_LICENSE_PLATE_ALREADY_EXISTS);
      }
      containerSemiTrailer.setLicensePlate(licensePlate);
    }

    String numberOfAxles = String.valueOf(updates.get("numberOfAxles"));
    if (updates.get("numberOfAxles") != null && !Tool.isEqual(containerSemiTrailer.getNumberOfAxles(), numberOfAxles)) {
      containerSemiTrailer.setNumberOfAxles(Integer.valueOf(numberOfAxles));
    }

    String type = String.valueOf(updates.get("type"));
    if (updates.get("type") != null && !Tool.isEqual(containerSemiTrailer.getType(), type)) {
      try {
        containerSemiTrailer.setType(EnumTrailerType.findByName(type).name());
      } catch (Exception e) {
        throw new NotFoundException(ErrorConstant.TRAILER_TYPE_NOT_FOUND);
      }
    }

    String unitOfMeasurement = String.valueOf(updates.get("unitOfMeasurement"));
    if (updates.get("unitOfMeasurement") != null
        && !Tool.isEqual(containerSemiTrailer.getUnitOfMeasurement(), unitOfMeasurement)) {
      try {
        containerSemiTrailer.setUnitOfMeasurement(EnumUnit.findByName(unitOfMeasurement).name());
      } catch (Exception e) {
        throw new NotFoundException(ErrorConstant.UNIT_OF_MEASUREMENT_NOT_FOUND);
      }
    }
    ContainerSemiTrailer _containerSemiTrailer = containerSemiTrailerRepository.save(containerSemiTrailer);
    return _containerSemiTrailer;
  }

  @Override
  public void removeContainerSemiTrailer(Long id, String username) {

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));

    if (!containerSemiTrailer.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    Collection<Container> containers = containerRepository.findByTrailer(id, EnumSupplyStatus.COMBINED.name(),
        EnumSupplyStatus.BIDDING.name());
    if (containers != null) {
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorConstant.CONTAINER_BUSY);
        }
      });
    }
    containerSemiTrailerRepository.delete(containerSemiTrailer);

  }

  @Override
  public Page<ContainerSemiTrailer> getContainerSemiTrailersByForwarder(String username, PaginationRequest request) {

    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ContainerSemiTrailer> pages = containerSemiTrailerRepository.findByForwarder(username, pageRequest);
    return pages;

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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));
    return containerSemiTrailer;
  }

}
