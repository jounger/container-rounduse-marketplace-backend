package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
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
  public void saveContainerType(ContainerTypeRequest request) {
    if (containerTypeRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("ERROR: Type name already exists.");
    }
    try {
      ContainerType containerType = new ContainerType();
      containerType.setName(request.getName());
      containerType.setDescription(request.getDescription());
      containerType.setTareWeight(Float.parseFloat(request.getTareWeight()));
      containerType.setPayloadCapacity(Float.parseFloat(request.getPayloadCapacity()));
      containerType.setCubicCapacity(Float.parseFloat(request.getCubicCapacity()));
      containerType.setInternalLength(Float.parseFloat(request.getInternalLength()));
      containerType.setInternalHeight(Float.parseFloat(request.getInternalHeight()));
      containerType.setInternalWidth(Float.parseFloat(request.getInternalWidth()));
      containerType.setDoorOpeningHeight(Float.parseFloat(request.getDoorOpeningHeight()));
      containerType.setDoorOpeningWidth(Float.parseFloat(request.getDoorOpeningWidth()));
      containerTypeRepository.save(containerType);
    } catch (Exception e) {
      throw new InternalException("ERROR: Parameter must be float");
    }

  }

  @Override
  public void updateContainerType(ContainerTypeRequest request) {
    try {
      ContainerType containerType = containerTypeRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));
      containerType.setName(request.getName());
      containerType.setDescription(request.getDescription());
      containerType.setTareWeight(Float.parseFloat(request.getTareWeight()));
      containerType.setPayloadCapacity(Float.parseFloat(request.getPayloadCapacity()));
      containerType.setCubicCapacity(Float.parseFloat(request.getCubicCapacity()));
      containerType.setInternalLength(Float.parseFloat(request.getInternalLength()));
      containerType.setInternalHeight(Float.parseFloat(request.getInternalHeight()));
      containerType.setInternalWidth(Float.parseFloat(request.getInternalWidth()));
      containerType.setDoorOpeningHeight(Float.parseFloat(request.getDoorOpeningHeight()));
      containerType.setDoorOpeningWidth(Float.parseFloat(request.getDoorOpeningWidth()));
      containerTypeRepository.save(containerType);
    } catch (Exception e) {
      throw new InternalException("ERROR: Parameter must be float");
    }
  }

  @Override
  public void deleteContainerType(Long id) {
    ContainerType containerType = containerTypeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));
    containerTypeRepository.delete(containerType);
  }

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

}
