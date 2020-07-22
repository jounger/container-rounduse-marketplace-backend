package com.crm.services;

import java.util.List;
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

  List<Container> getContainersByBidAndStatus(Long id, String status);

  Page<Container> getContainers(PaginationRequest request);

  Container createContainer(Long id, Long userId, ContainerRequest request);

  Container updateContainer(Long userId, ContainerRequest request);

  Container editContainer(Map<String, Object> updates, Long id, Long userId);

  void removeContainer(Long id, Long userId);

}
