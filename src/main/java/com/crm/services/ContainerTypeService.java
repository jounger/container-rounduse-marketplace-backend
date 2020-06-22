package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerType;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerTypeService {

  Page<ContainerType> getContainerTypes(PaginationRequest request);

  ContainerType getContainerTypeById(Long id);

  void createContainerType(ContainerTypeRequest request);

  ContainerType updateContainerType(ContainerTypeRequest request);

  void removeContainerType(Long id);

}
