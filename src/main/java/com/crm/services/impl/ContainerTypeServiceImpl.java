package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumUnit;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.ContainerType;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerTypeRepository;
import com.crm.services.ContainerTypeService;

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
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));
    return containerType;
  }

  @Override
  public ContainerType getContainerTypeByName(String name) {
    ContainerType containerType = containerTypeRepository.findByName(name)
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));
    return containerType;
  }

  @Override
  public ContainerType createContainerType(ContainerTypeRequest request) {
    if (containerTypeRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("ERROR: ContainerType already exists.");
    }
    ContainerType containerType = new ContainerType();
    containerType.setName(request.getName());
    containerType.setDescription(request.getDescription());

    containerType.setTareWeight(request.getTareWeight());

    containerType.setPayloadCapacity(request.getPayloadCapacity());

    containerType.setCubicCapacity(request.getCubicCapacity());

    containerType.setInternalLength(request.getInternalLength());
    containerType.setInternalHeight(request.getInternalHeight());
    containerType.setInternalWidth(request.getInternalWidth());
    containerType.setDoorOpeningHeight(request.getDoorOpeningHeight());
    containerType.setDoorOpeningWidth(request.getDoorOpeningWidth());
    try {
      containerType.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    } catch (Exception e) {
      throw new NotFoundException("ERROR: UnitOfMeasurement is not found.");
    }
    containerTypeRepository.save(containerType);
    return containerType;
  }

  @Override
  public ContainerType updateContainerType(ContainerTypeRequest request) {

    ContainerType containerType = containerTypeRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));

    if (containerTypeRepository.existsByName(request.getName())) {
      if (request.getName().equals(containerType.getName())) {
      } else {
        throw new DuplicateRecordException("ERROR: ContainerType already exists.");
      }
    }
    containerType.setName(request.getName());

    containerType.setDescription(request.getDescription());
    containerType.setTareWeight(request.getTareWeight());
    containerType.setPayloadCapacity(request.getPayloadCapacity());
    containerType.setCubicCapacity(request.getCubicCapacity());
    containerType.setInternalLength(request.getInternalLength());
    containerType.setInternalHeight(request.getInternalHeight());
    containerType.setInternalWidth(request.getInternalWidth());
    containerType.setDoorOpeningHeight(request.getDoorOpeningHeight());
    containerType.setDoorOpeningWidth(request.getDoorOpeningWidth());
    try {
      containerType.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    } catch (Exception e) {
      throw new NotFoundException("ERROR: UnitOfMeasurement is not found.");
    }
    containerTypeRepository.save(containerType);
    return containerType;
  }

  @Override
  public void removeContainerType(Long id) {
    if (containerTypeRepository.existsById(id)) {
      containerTypeRepository.deleteById(id);
    } else {
      new NotFoundException("ERROR: ContainerType is not found.");
    }
  }

  @Override
  public ContainerType editContainerType(Map<String, Object> updates, Long id) {

    ContainerType containerType = containerTypeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));

    String name = (String) updates.get("name");
    if (name != null && !name.isEmpty()) {
      if (containerTypeRepository.existsByName(name)) {
        if (name.equals(containerType.getName())) {
        } else {
          throw new DuplicateRecordException("ERROR: ContainerType already exists.");
        }
      }
    }

    String description = (String) updates.get("description");
    if (description != null && !description.isEmpty()) {
      containerType.setDescription(description);
    }

    String tareWeight = (String) updates.get("tareWeight");
    if (tareWeight != null && !tareWeight.isEmpty()) {
      containerType.setTareWeight(Double.valueOf(tareWeight));
    }

    String payloadCapacity = (String) updates.get("payloadCapacity");
    if (payloadCapacity != null && !payloadCapacity.isEmpty()) {
      containerType.setPayloadCapacity(Double.valueOf(payloadCapacity));
    }

    String cubicCapacity = (String) updates.get("cubicCapacity");
    if (cubicCapacity != null && !cubicCapacity.isEmpty()) {
      containerType.setCubicCapacity(Double.valueOf(cubicCapacity));
    }

    String internalLength = (String) updates.get("internalLength");
    if (internalLength != null && !internalLength.isEmpty()) {
      containerType.setInternalLength(Double.valueOf(internalLength));
    }

    String internalHeight = (String) updates.get("internalHeight");
    if (internalHeight != null && !internalHeight.isEmpty()) {
      containerType.setInternalHeight(Double.valueOf(internalHeight));
    }

    String internalWidth = (String) updates.get("internalWidth");
    if (internalWidth != null && !internalWidth.isEmpty()) {
      containerType.setInternalWidth(Double.valueOf(internalWidth));
    }

    String doorOpeningHeight = (String) updates.get("doorOpeningHeight");
    if (doorOpeningHeight != null && !doorOpeningHeight.isEmpty()) {
      containerType.setDoorOpeningHeight(Double.valueOf(doorOpeningHeight));
    }

    String doorOpeningWidth = (String) updates.get("doorOpeningWidth");
    if (doorOpeningWidth != null && !doorOpeningWidth.isEmpty()) {
      containerType.setDoorOpeningWidth(Double.valueOf(doorOpeningWidth));
    }

    String unitOfMeasurement = (String) updates.get("unitOfMeasurement");
    if (unitOfMeasurement != null && !unitOfMeasurement.isEmpty()) {
      try {
        containerType.setUnitOfMeasurement(EnumUnit.findByName(unitOfMeasurement).name());
      } catch (Exception e) {
        throw new NotFoundException("ERROR: UnitOfMeasurement is not found.");
      }
    }

    containerTypeRepository.save(containerType);
    return containerType;
  }

}
