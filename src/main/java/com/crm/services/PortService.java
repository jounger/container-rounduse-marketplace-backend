package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Port;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PortRequest;

public interface PortService {

  Page<Port> getPorts(PaginationRequest request);

  Port getPortById(Long id);

  void createPort(PortRequest request);

  Port updatePort(PortRequest request);

  void removePort(Long id);
}
