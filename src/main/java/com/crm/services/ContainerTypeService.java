package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerType;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerTypeService {

  Page<ContainerType> getContainerTypes(PaginationRequest request);

  ContainerType getContainerTypeById(Long id);

  ContainerType getContainerTypeByName(String name);

  ContainerType createContainerType(ContainerTypeRequest request);

  ContainerType updateContainerType(ContainerTypeRequest request);

  ContainerType editContainerType(Map<String, Object> updates, Long id);

  void removeContainerType(Long id);

}
