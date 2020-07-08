package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerTractor;
import com.crm.payload.request.ContainerTractorRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerTractorService {

  ContainerTractor getContainerTractorById(Long id);

  Page<ContainerTractor> getContainerTractors(PaginationRequest request);

  ContainerTractor createContainerTractor(Long id, ContainerTractorRequest request);

  ContainerTractor updateContainerTractor(Long userId, ContainerTractorRequest request);

  ContainerTractor editContainerTractor(Map<String, Object> updates, Long id, Long userId);

  void removeContainerTractor(Long id, Long userId);
}
