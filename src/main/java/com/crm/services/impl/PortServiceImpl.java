package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Port;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PortRequest;
import com.crm.repository.PortRepository;
import com.crm.services.PortService;

@Service
public class PortServiceImpl implements PortService {

  @Autowired
  private PortRepository portRepository;

  @Override
  public void createPort(PortRequest request) {
    if (portRepository.existsByNameCode(request.getNameCode())) {
      throw new DuplicateRecordException("ERROR: Port already exists.");
    }
    Port port = new Port();
    port.setFullname(request.getFullname());
    port.setNameCode(request.getNameCode());
    port.setAddress(request.getAddress());
    portRepository.save(port);

  }

  @Override
  public Port updatePort(PortRequest request) {
    Port port = portRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));

    port.setFullname(request.getFullname());
    port.setNameCode(request.getNameCode());
    port.setAddress(request.getAddress());

    portRepository.save(port);
    return port;
  }

  @Override
  public void removePort(Long id) {
    if (portRepository.existsById(id)) {
      portRepository.deleteById(id);
    } else {
      throw new NotFoundException("ERROR: Port is not found.");
    }
  }

  @Override
  public Page<Port> getPorts(PaginationRequest request) {
    Page<Port> pages = portRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public Port getPortById(Long id) {
    Port port = portRepository.findById(id).orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    return port;
  }

}
