package com.crm.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email") })
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" }, allowGetters = true)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "username")
  @NotBlank
  @Size(min = 2, max = 20)
  private String username;

  @Column(name = "password")
  @Size(min = 6, max = 120)
  private String password;

  @Column(name = "phone")
  @NotBlank
  @Size(min = 10, max = 10)
  private String phone;

  @Column(name = "email")
  @NotBlank
  @Size(min = 5, max = 50)
  @Email
  private String email;

  @Column(name = "address")
  @NotBlank
  @Size(min = 5, max = 200)
  private String address;

  // EnumUserStatus
  @Column(name = "status")
  @NotBlank
  @Size(min = 2, max = 20)
  private String status;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;

  @OneToMany(mappedBy = "recipient")
  private Collection<Notification> notifications = new ArrayList<>();

  @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
  private Collection<Role> roles = new ArrayList<>();

  @OneToMany(mappedBy = "sender")
  private Collection<Feedback> feedbacks = new ArrayList<>();

  // DO NOT DELETE CODE BELLOW

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    User other = (User) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    return true;
  }

}
