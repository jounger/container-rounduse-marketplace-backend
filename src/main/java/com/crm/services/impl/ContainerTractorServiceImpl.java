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
import com.crm.exception.DuplicateRecordException;
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

    containerTractor.setNumberOfAxles(request.getNumberOfAxles());

    containerTractor.setForwarder(forwarder);

    containerTractorRepository.save(containerTractor);
    return containerTractor;
  }

  @Override
  public ContainerTractor updateContainerTractor(Long userId, ContainerTractorRequest request) {

    if (forwarderRepository.existsById(userId)) {

      ContainerTractor containerTractor = containerTractorRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));

      if (!containerTractor.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned containerTractor", userId));
      }

      Collection<Container> containers = containerRepository.findByTractor(request.getId(),
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
          && !request.getLicensePlate().equals(containerTractor.getLicensePlate())) {
        throw new DuplicateRecordException("Error: LicensePlate has been existed");
      } else {
        containerTractor.setLicensePlate(request.getLicensePlate());
      }

      containerTractor.setNumberOfAxles(request.getNumberOfAxles());

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

      if (!containerTractor.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned containerTractor", userId));
      }

      Collection<Container> containers = containerRepository.findByTractor(id, EnumSupplyStatus.COMBINED.name(),
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

      String licensePlate = (String) updates.get("licensePlate");
      if (licensePlate != null && !licensePlate.isEmpty() && !licensePlate.equals(containerTractor.getLicensePlate())) {
        if (vehicleRepository.existsByLicensePlate(licensePlate)) {
          throw new DuplicateRecordException("Error: LicensePlate has been existed");
        } else {
          containerTractor.setLicensePlate(licensePlate);
        }
      }

      String numberOfAxles = (String) updates.get("numberOfAxles");
      if (numberOfAxles != null && !numberOfAxles.isEmpty()) {
        containerTractor.setNumberOfAxles(Integer.valueOf(numberOfAxles));
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

      if (!containerTractor.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned containerTractor", userId));
      }

      Collection<Container> containers = containerRepository.findByTractor(id, EnumSupplyStatus.COMBINED.name(),
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
      containerTractorRepository.delete(containerTractor);
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }

  }

  @Override
  public Page<ContainerTractor> getContainerTractorsByForwarder(Long userId, PaginationRequest request) {
    if (forwarderRepository.existsById(userId)) {
      PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
          Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<ContainerTractor> pages = containerTractorRepository.findByForwarder(userId, pageRequest);
      return pages;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
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
        .orElseThrow(() -> new NotFoundException("ERROR: containerTractor is not found."));
    return containerTractor;
  }

}
