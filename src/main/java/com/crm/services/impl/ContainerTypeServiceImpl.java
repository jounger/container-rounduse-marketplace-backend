package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
    Page<ContainerType> pages = containerTypeRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public ContainerType getContainerTypeById(Long id) {
    ContainerType containerType = containerTypeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));
    return containerType;
  }

  @Override
  public void createContainerType(ContainerTypeRequest request) {
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
    containerTypeRepository.save(containerType);

  }

  @Override
  public ContainerType updateContainerType(ContainerTypeRequest request) {

    ContainerType containerType = containerTypeRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));
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
    if (name != null) {
      containerType.setName(name);
    }
    String description = (String) updates.get("description");
    if (description != null) {
      containerType.setDescription(description);
    }
    Double tareWeight = (Double) updates.get("tareWeight");
    if (tareWeight != null) {
      containerType.setTareWeight(tareWeight);
    }

    Double payloadCapacity = (Double) updates.get("payloadCapacity");
    if (payloadCapacity != null) {
      containerType.setPayloadCapacity(payloadCapacity);
    }

    Double cubicCapacity = (Double) updates.get("cubicCapacity");
    if (cubicCapacity != null) {
      containerType.setCubicCapacity(cubicCapacity);
    }

    Double internalLength = (Double) updates.get("internalLength");
    if (internalLength != null) {
      containerType.setInternalLength(internalLength);
    }

    Double internalHeight = (Double) updates.get("internalHeight");
    if (internalHeight != null) {
      containerType.setInternalHeight(internalHeight);
    }

    Double internalWidth = (Double) updates.get("internalWidth");
    if (internalWidth != null) {
      containerType.setInternalWidth(internalWidth);
    }

    Double doorOpeningHeight = (Double) updates.get("doorOpeningHeight");
    if (doorOpeningHeight != null) {
      containerType.setDoorOpeningHeight(doorOpeningHeight);
    }

    Double doorOpeningWidth = (Double) updates.get("doorOpeningWidth");
    if (doorOpeningWidth != null) {
      containerType.setDoorOpeningWidth(doorOpeningWidth);
    }

    containerTypeRepository.save(containerType);
    return containerType;
  }

}
