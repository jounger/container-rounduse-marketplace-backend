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
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Container;
import com.crm.models.ContainerTractor;
import com.crm.models.Forwarder;
import com.crm.payload.request.ContainerTractorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerTractorRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.VehicleRepository;
import com.crm.services.ContainerTractorService;
import com.crm.specification.builder.ContainerTractorSpecificationsBuilder;

@Service
public class ContainerTractorServiceImpl implements ContainerTractorService {

  @Autowired
  ContainerTractorRepository containerTractorRepository;

  @Autowired
  ForwarderRepository forwarderRepository;

  @Autowired
  VehicleRepository vehicleRepository;

  @Autowired
  ContainerRepository containerRepository;

  @Override
  public ContainerTractor getContainerTractorById(Long id) {
    ContainerTractor containerTractor = containerTractorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));
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
  public ContainerTractor createContainerTractor(String username, ContainerTractorRequest request) {

    ContainerTractor containerTractor = new ContainerTractor();

    Forwarder forwarder = forwarderRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND));

    if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
      throw new DuplicateRecordException(ErrorMessage.VEHICLE_LICENSE_PLATE_ALREADY_EXISTS);
    } else {
      containerTractor.setLicensePlate(request.getLicensePlate());
    }

    containerTractor.setNumberOfAxles(request.getNumberOfAxles());

    containerTractor.setForwarder(forwarder);

    ContainerTractor _containerTractor = containerTractorRepository.save(containerTractor);
    return _containerTractor;
  }

  @Override
  public ContainerTractor updateContainerTractor(String username, ContainerTractorRequest request) {

    ContainerTractor containerTractor = containerTractorRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));

    if (!containerTractor.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    Collection<Container> containers = containerRepository.findByTractor(request.getId(),
        EnumSupplyStatus.COMBINED.name(), EnumSupplyStatus.BIDDING.name());
    if (containers != null) {
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorMessage.TRACTOR_BUSY);
        }
      });
    }

    if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())
        && !request.getLicensePlate().equals(containerTractor.getLicensePlate())) {
      throw new DuplicateRecordException(ErrorMessage.VEHICLE_LICENSE_PLATE_ALREADY_EXISTS);
    } else {
      containerTractor.setLicensePlate(request.getLicensePlate());
    }

    containerTractor.setNumberOfAxles(request.getNumberOfAxles());

    ContainerTractor _containerTractor = containerTractorRepository.save(containerTractor);
    return _containerTractor;

  }

  @Override
  public ContainerTractor editContainerTractor(Map<String, Object> updates, Long id, String username) {

    ContainerTractor containerTractor = containerTractorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));

    if (!containerTractor.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    Collection<Container> containers = containerRepository.findByTractor(id, EnumSupplyStatus.COMBINED.name(),
        EnumSupplyStatus.BIDDING.name());
    if (containers != null) {
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorMessage.CONTAINER_BUSY);
        }
      });
    }

    String licensePlate = String.valueOf(updates.get("licensePlate"));
    if (updates.get("licensePlate") != null && !Tool.isEqual(containerTractor.getLicensePlate(), licensePlate)) {
      if (vehicleRepository.existsByLicensePlate(licensePlate)) {
        throw new DuplicateRecordException(ErrorMessage.VEHICLE_LICENSE_PLATE_ALREADY_EXISTS);
      }
      containerTractor.setLicensePlate(licensePlate);
    }

    String numberOfAxles = String.valueOf(updates.get("numberOfAxles"));
    if (updates.get("numberOfAxles") != null && !Tool.isEqual(containerTractor.getNumberOfAxles(), numberOfAxles)) {
      containerTractor.setNumberOfAxles(Integer.valueOf(numberOfAxles));
    }

    ContainerTractor _containerTractor = containerTractorRepository.save(containerTractor);
    return _containerTractor;
  }

  @Override
  public void removeContainerTractor(Long id, String username) {

    ContainerTractor containerTractor = containerTractorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));

    if (!containerTractor.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    Collection<Container> containers = containerRepository.findByTractor(id, EnumSupplyStatus.COMBINED.name(),
        EnumSupplyStatus.BIDDING.name());
    if (containers != null) {
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorMessage.CONTAINER_BUSY);
        }
      });
    }
    containerTractorRepository.delete(containerTractor);

  }

  @Override
  public Page<ContainerTractor> getContainerTractorsByForwarder(String username, PaginationRequest request) {

    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ContainerTractor> pages = containerTractorRepository.findByForwarder(username, pageRequest);
    return pages;

  }

  @Override
  public Page<ContainerTractor> searchContainerTractors(PaginationRequest request, String search) {
    ContainerTractorSpecificationsBuilder builder = new ContainerTractorSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<ContainerTractor> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<ContainerTractor> pages = containerTractorRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public ContainerTractor getContainerTractorByLicensePlate(String licensePlate) {
    ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(licensePlate)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_BUSY));
    return containerTractor;
  }

}
