package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
@Table(name = "supplier")
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "user_id")
public class Supplier extends User {

  @Column(name = "website", length = 50)
  @NotBlank
  @Size(min = 5, max = 100)
  private String website;

  @Column(name = "contact_person", length = 100)
  @Size(min = 5, max = 50)
  private String contactPerson;

  
  @Column(name = "company_name", length = 100)
  @NotBlank
  @Size(min = 5, max = 100)
  private String companyName;

  @Column(name = "company_code", length = 10, unique = true)
  @NotBlank
  @Size(min = 2, max = 10)
  private String companyCode;

  @Column(name = "description", length = 100)
  @NotBlank
  @Size(min = 5, max = 200)
  private String companyDescription;

  @Column(name = "company_address", length = 200)
  @NotBlank
  @Size(min = 5, max = 200)
  private String companyAddress;
  
  @Column(name = "tin", length = 20)
  @NotBlank
  @Size(min = 5, max = 20)
  private String tin;

  @Column(name = "fax", length = 20)
  @NotBlank
  @Size(min = 5, max = 20)
  private String fax;

  @Column(name = "rating_value")
  private float ratingValue;

  @OneToMany(mappedBy = "receiver")
  private Collection<Rating> receivedRatings = new ArrayList<>();

  @OneToMany(mappedBy = "sender")
  private Collection<Rating> sentRatings = new ArrayList<>();
}
