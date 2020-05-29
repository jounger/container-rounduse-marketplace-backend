package com.crm.dao.impl;

import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.crm.dao.UserDao;
import com.crm.models.User;

@Repository
public class UserDaoImpl implements UserDao{

  HibernateTemplate template;
  
  public void setTemplate(HibernateTemplate template) {  
    this.template = template;  
  }  
  
  @Override
  public void saveUser(User user) {
    template.save(user);
    
  }

}
