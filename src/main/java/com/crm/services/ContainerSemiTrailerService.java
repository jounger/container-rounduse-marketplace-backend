package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerSemiTrailer;
import com.crm.payload.request.ContainerSemiTrailerRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerSemiTrailerService {

  ContainerSemiTrailer getContainerSemiTrailerById(Long id);

  Page<ContainerSemiTrailer> getContainerSemiTrailers(PaginationRequest request);

  Page<ContainerSemiTrailer> getContainerSemiTrailersByForwarder(Long userId, PaginationRequest request);

  Page<ContainerSemiTrailer> searchContainerSemiTrailers(PaginationRequest request, String search);

  ContainerSemiTrailer createContainerSemiTrailer(Long userId, ContainerSemiTrailerRequest request);

  ContainerSemiTrailer updateContainerSemiTrailer(Long userId, ContainerSemiTrailerRequest request);

  ContainerSemiTrailer editContainerSemiTrailer(Map<String, Object> updates, Long id, Long userId);

  void removeContainerSemiTrailer(Long id, Long userId);
}
