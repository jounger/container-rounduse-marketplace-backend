package com.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.crm.enums.EnumRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="ROLE")
public class Role {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Enumerated(EnumType.STRING)
  @Column(length=20)
  private EnumRole name;
  
}
