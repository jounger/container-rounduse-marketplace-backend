package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerTractor;
import com.crm.payload.request.ContainerTractorRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerTractorService {

  ContainerTractor getContainerTractorById(Long id);

  ContainerTractor getContainerTractorByLicensePlate(String licensePlate);

  Page<ContainerTractor> getContainerTractors(PaginationRequest request);

  Page<ContainerTractor> getContainerTractorsByForwarder(String username, PaginationRequest request);

  Page<ContainerTractor> searchContainerTractors(PaginationRequest request, String search);

  ContainerTractor createContainerTractor(String username, ContainerTractorRequest request);

  ContainerTractor updateContainerTractor(String username, ContainerTractorRequest request);

  ContainerTractor editContainerTractor(Map<String, Object> updates, Long id, String username);

  void removeContainerTractor(Long id, String username);
}
