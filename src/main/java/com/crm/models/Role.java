package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
@Table(name="role")
public class Role {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(length=20, unique = true)
  private String name;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  private Collection<User> users = new ArrayList<User>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "role_permission",
  			joinColumns = @JoinColumn(name = "role_id"),
  			inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private Set<Permission> permissions = new HashSet<Permission>();
}
