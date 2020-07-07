package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContainerSemiTrailer;
import com.crm.payload.request.ContainerSemiTrailerRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContainerSemiTrailerService {

  ContainerSemiTrailer getContainerSemiTrailerById(Long id);

  Page<ContainerSemiTrailer> getContainerSemiTrailers(PaginationRequest request);

  ContainerSemiTrailer createContainerSemiTrailer(Long id, ContainerSemiTrailerRequest request);

  ContainerSemiTrailer updateContainerSemiTrailer(Long id, ContainerSemiTrailerRequest request);

  ContainerSemiTrailer editContainerSemiTrailer(Map<String, Object> updates, Long id, Long userId);

  void removeContainerSemiTrailer(Long id, Long userId);
}
