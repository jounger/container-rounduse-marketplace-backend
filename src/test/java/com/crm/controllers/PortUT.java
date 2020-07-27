package com.crm.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.crm.models.Port;
import com.crm.repository.PortRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PortUT {
  
  @Autowired
  private PortRepository portRepository;

  @Test
  void createPort() {
    Port port = new Port();
    port.setFullname("Cang Quang Ninh");
    port.setNameCode("QUANGNINHPORT");
    port.setAddress("Thanh pho Quang Ninh");
    port.setCreatedAt(new Date());
    port.setUpdatedAt(new Date());
    
    Port savedPort = portRepository.save(port);
    
    assertNotNull(savedPort);
  }

}
