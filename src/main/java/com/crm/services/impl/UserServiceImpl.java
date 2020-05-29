package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.dao.UserDao;
import com.crm.models.User;
import com.crm.services.UserService;

@Service
public class UserServiceImpl implements UserService {
  
  @Autowired
  private UserDao userDao;
  
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void saveUser(User user) {
    String encoder = passwordEncoder.encode(user.getPassword());
    user.setPassword(encoder);
    userDao.saveUser(user);
    
  }

}
