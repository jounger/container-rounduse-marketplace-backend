package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Container;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerService {

  Container getContainerById(Long id);

  Page<Container> getContainersByBillOfLading(Long id, PaginationRequest request);

  Page<Container> getContainersByInbound(Long id, PaginationRequest request);

  Page<Container> getContainersByBid(Long id, PaginationRequest request);

  Page<Container> getContainers(PaginationRequest request);

  Container createContainer(Long id, ContainerRequest request);

  Container updateContainer(ContainerRequest request);

  Container editContainer(Map<String, Object> updates, Long id);

  void removeContainer(Long id);

}
