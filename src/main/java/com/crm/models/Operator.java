package com.crm.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
@Table(name = "operator")
@PrimaryKeyJoinColumn(name = "user_id")
public class Operator extends User {

  @Column(name = "fullname")
  @NotBlank
  @Size(min = 3, max = 30)
  private String fullname;

  @Column(name = "is_root")
  private Boolean isRoot;
}
