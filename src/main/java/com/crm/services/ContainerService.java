package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Container;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerService {

  Container getContainerById(Long id);

  Page<Container> getContainersByForwarder(Long id, PaginationRequest request);

  Page<Container> getContainers(PaginationRequest request);

  void createContainer(ContainerRequest request);

  Container updateContainer(ContainerRequest request);

  void removeContainer(Long id);

}
