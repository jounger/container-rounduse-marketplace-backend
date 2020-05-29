package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.crm.dao.UserDao;
import com.crm.models.User;
import com.crm.services.UserService;

public class UserServiceImpl implements UserService {
  
  @Autowired
  private UserDao userDao;

  @Override
  public void saveUser(User user) {
    userDao.saveUser(user);
    
  }

}
