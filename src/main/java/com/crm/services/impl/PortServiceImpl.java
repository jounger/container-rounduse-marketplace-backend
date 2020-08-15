package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
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
  public Port createPort(PortRequest request) {
    if (portRepository.existsByNameCode(request.getNameCode())) {
      throw new DuplicateRecordException(ErrorMessage.PORT_ALREADY_EXISTS);
    }
    Port port = new Port();
    port.setFullname(request.getFullname());
    port.setNameCode(request.getNameCode());
    port.setAddress(request.getAddress());
    Port _port = portRepository.save(port);
    return _port;
  }

  @Override
  public Port updatePort(PortRequest request) {
    Port port = portRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.PORT_NOT_FOUND));

    if (portRepository.existsByNameCode(request.getNameCode())) {
      if (request.getNameCode().equals(port.getNameCode())) {
      } else {
        throw new DuplicateRecordException(ErrorMessage.PORT_ALREADY_EXISTS);
      }
    }
    port.setNameCode(request.getNameCode());

    port.setFullname(request.getFullname());
    port.setAddress(request.getAddress());

    Port _port = portRepository.save(port);
    return _port;
  }

  @Override
  public void removePort(Long id) {
    if (portRepository.existsById(id)) {
      portRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.PORT_NOT_FOUND);
    }
  }

  @Override
  public Page<Port> getPorts(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Port> pages = portRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public Port getPortById(Long id) {
    Port port = portRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.PORT_NOT_FOUND));
    return port;
  }

  @Override
  public Port editPort(Map<String, Object> updates, Long id) {
    Port port = portRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.PORT_NOT_FOUND));

    String fullname = String.valueOf(updates.get("fullname"));
    if (updates.get("fullname") != null && !Tool.isEqual(port.getFullname(), fullname)) {
      port.setFullname(fullname);
    }

    String address = String.valueOf(updates.get("address"));
    if (updates.get("address") != null && !Tool.isEqual(port.getAddress(), address)) {
      port.setAddress(address);
    }

    String nameCode = String.valueOf(updates.get("nameCode"));
    if (updates.get("nameCode") != null && !Tool.isEqual(port.getNameCode(), nameCode)) {
      if (portRepository.existsByNameCode(nameCode)) {
        throw new DuplicateRecordException(ErrorMessage.PORT_ALREADY_EXISTS);
      }
      port.setNameCode(nameCode);
    }

    Port _port = portRepository.save(port);
    return _port;
  }

  @Override
  public Port getPortByNameCode(String nameCode) {
    Port port = portRepository.findByNameCode(nameCode)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.PORT_NOT_FOUND));
    return port;
  }

}
