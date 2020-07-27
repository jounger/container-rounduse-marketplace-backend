package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerSemiTrailer;
import com.crm.payload.request.ContainerSemiTrailerRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerSemiTrailerService {

  ContainerSemiTrailer getContainerSemiTrailerById(Long id);

  ContainerSemiTrailer getContainerSemiTrailerByLicensePlate(String licensePlate);

  Page<ContainerSemiTrailer> getContainerSemiTrailers(PaginationRequest request);

  Page<ContainerSemiTrailer> getContainerSemiTrailersByForwarder(String username, PaginationRequest request);

  Page<ContainerSemiTrailer> searchContainerSemiTrailers(PaginationRequest request, String search);

  ContainerSemiTrailer createContainerSemiTrailer(String username, ContainerSemiTrailerRequest request);

  ContainerSemiTrailer updateContainerSemiTrailer(String username, ContainerSemiTrailerRequest request);

  ContainerSemiTrailer editContainerSemiTrailer(Map<String, Object> updates, Long id, String username);

  void removeContainerSemiTrailer(Long id, String username);
}
