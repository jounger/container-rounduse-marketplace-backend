package com.crm.controllers;

import java.util.Arrays;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.crm.models.Role;
import com.crm.models.User;
import com.crm.security.services.UserDetailsImpl;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {
    Role role = new Role();
    role.setId(2L);
    role.setName("ROLE_MODERATOR");
    User basicUser = new User();
    basicUser.setUsername("moderator");
    basicUser.setPassword("123456");
    basicUser.setRoles(Arrays.asList(role));
    UserDetailsImpl moderator = UserDetailsImpl.build(basicUser);

    return new InMemoryUserDetailsManager(moderator);
  }
}
