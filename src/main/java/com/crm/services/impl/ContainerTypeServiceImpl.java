package com.crm.services.impl;

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
import com.crm.enums.EnumUnit;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.ContainerType;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerTypeRepository;
import com.crm.services.ContainerTypeService;
import com.crm.specification.builder.ContainerTypeSpecificationsBuilder;

@Service
public class ContainerTypeServiceImpl implements ContainerTypeService {

  @Autowired
  private ContainerTypeRepository containerTypeRepository;

  @Override
  public Page<ContainerType> getContainerTypes(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ContainerType> pages = containerTypeRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public ContainerType getContainerTypeById(Long id) {
    ContainerType containerType = containerTypeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND));
    return containerType;
  }

  @Override
  public ContainerType getContainerTypeByName(String name) {
    ContainerType containerType = containerTypeRepository.findByName(name)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND));
    return containerType;
  }

  @Override
  public ContainerType createContainerType(ContainerTypeRequest request) {
    if (containerTypeRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException(ErrorMessage.CONTAINER_TYPE_ALREADY_EXISTS);
    }
    ContainerType containerType = new ContainerType();
    containerType.setName(request.getName());
    containerType.setDescription(request.getDescription());

    containerType.setTareWeight(request.getTareWeight());

    containerType.setGrossWeight(request.getGrossWeight());

    containerType.setCubicCapacity(request.getCubicCapacity());

    containerType.setInternalLength(request.getInternalLength());
    containerType.setInternalHeight(request.getInternalHeight());
    containerType.setInternalWidth(request.getInternalWidth());
    containerType.setDoorOpeningHeight(request.getDoorOpeningHeight());
    containerType.setDoorOpeningWidth(request.getDoorOpeningWidth());
    try {
      containerType.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    } catch (Exception e) {
      throw new NotFoundException(ErrorMessage.UNIT_OF_MEASUREMENT_NOT_FOUND);
    }
    ContainerType _containerType = containerTypeRepository.save(containerType);
    return _containerType;
  }

  @Override
  public ContainerType updateContainerType(ContainerTypeRequest request) {

    ContainerType containerType = containerTypeRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND));

    if (containerTypeRepository.existsByName(request.getName())) {
      if (request.getName().equals(containerType.getName())) {
      } else {
        throw new DuplicateRecordException(ErrorMessage.CONTAINER_TYPE_ALREADY_EXISTS);
      }
    }
    containerType.setName(request.getName());

    containerType.setDescription(request.getDescription());
    containerType.setTareWeight(request.getTareWeight());
    containerType.setGrossWeight(request.getGrossWeight());
    containerType.setCubicCapacity(request.getCubicCapacity());
    containerType.setInternalLength(request.getInternalLength());
    containerType.setInternalHeight(request.getInternalHeight());
    containerType.setInternalWidth(request.getInternalWidth());
    containerType.setDoorOpeningHeight(request.getDoorOpeningHeight());
    containerType.setDoorOpeningWidth(request.getDoorOpeningWidth());
    try {
      containerType.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    } catch (Exception e) {
      throw new NotFoundException(ErrorMessage.UNIT_OF_MEASUREMENT_NOT_FOUND);
    }
    ContainerType _containerType = containerTypeRepository.save(containerType);
    return _containerType;
  }

  @Override
  public void removeContainerType(Long id) {
    if (containerTypeRepository.existsById(id)) {
      containerTypeRepository.deleteById(id);
    } else {
      new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND);
    }
  }

  @Override
  public ContainerType editContainerType(Map<String, Object> updates, Long id) {

    ContainerType containerType = containerTypeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND));

    String name = String.valueOf(updates.get("name"));
    if (updates.get("name") != null && !Tool.isEqual(containerType.getName(), name)) {
      if (containerTypeRepository.existsByName(name)) {
        throw new DuplicateRecordException(ErrorMessage.CONTAINER_TYPE_ALREADY_EXISTS);
      }
      containerType.setName(name);
    }

    String description = String.valueOf(updates.get("description"));
    if (updates.get("description") != null && !Tool.isEqual(containerType.getDescription(), description)) {
      containerType.setDescription(description);
    }

    String tareWeight = String.valueOf(updates.get("tareWeight"));
    if (updates.get("tareWeight") != null && !Tool.isEqual(containerType.getTareWeight(), tareWeight)) {
      containerType.setTareWeight(Double.valueOf(tareWeight));
    }

    String grossWeight = String.valueOf(updates.get("grossWeight"));
    if (updates.get("grossWeight") != null && !Tool.isEqual(containerType.getGrossWeight(), grossWeight)) {
      containerType.setGrossWeight(Double.valueOf(grossWeight));
    }

    String cubicCapacity = String.valueOf(updates.get("cubicCapacity"));
    if (updates.get("cubicCapacity") != null && !Tool.isEqual(containerType.getCubicCapacity(), cubicCapacity)) {
      containerType.setCubicCapacity(Double.valueOf(cubicCapacity));
    }

    String internalLength = String.valueOf(updates.get("internalLength"));
    if (updates.get("internalLength") != null && !Tool.isEqual(containerType.getInternalLength(), internalLength)) {
      containerType.setInternalLength(Double.valueOf(internalLength));
    }

    String internalHeight = String.valueOf(updates.get("internalHeight"));
    if (updates.get("internalHeight") != null && !Tool.isEqual(containerType.getInternalHeight(), internalHeight)) {
      containerType.setInternalHeight(Double.valueOf(internalHeight));
    }

    String internalWidth = String.valueOf(updates.get("internalWidth"));
    if (updates.get("internalWidth") != null && !Tool.isEqual(containerType.getInternalWidth(), internalWidth)) {
      containerType.setInternalWidth(Double.valueOf(internalWidth));
    }

    String doorOpeningHeight = String.valueOf(updates.get("doorOpeningHeight"));
    if (updates.get("doorOpeningHeight") != null
        && !Tool.isEqual(containerType.getDoorOpeningHeight(), doorOpeningHeight)) {
      containerType.setDoorOpeningHeight(Double.valueOf(doorOpeningHeight));
    }

    String doorOpeningWidth = String.valueOf(updates.get("doorOpeningWidth"));
    if (updates.get("doorOpeningWidth") != null
        && !Tool.isEqual(containerType.getDoorOpeningWidth(), doorOpeningWidth)) {
      containerType.setDoorOpeningWidth(Double.valueOf(doorOpeningWidth));
    }

    String unitOfMeasurement = String.valueOf(updates.get("unitOfMeasurement"));
    if (updates.get("unitOfMeasurement") != null
        && !Tool.isEqual(containerType.getUnitOfMeasurement(), unitOfMeasurement)) {
      try {
        containerType.setUnitOfMeasurement(EnumUnit.findByName(unitOfMeasurement).name());
      } catch (Exception e) {
        throw new NotFoundException(ErrorMessage.UNIT_OF_MEASUREMENT_NOT_FOUND);
      }
    }

    ContainerType _containerType = containerTypeRepository.save(containerType);
    return _containerType;
  }

  @Override
  public Page<ContainerType> searchContainerTypes(PaginationRequest request, String search) {
    ContainerTypeSpecificationsBuilder builder = new ContainerTypeSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<ContainerType> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<ContainerType> pages = containerTypeRepository.findAll(spec, page);
    // Return result
    return pages;
  }

}
