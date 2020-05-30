package com.crm.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
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
@Table(name="USER", uniqueConstraints = {
    @UniqueConstraint(columnNames="username"),
    @UniqueConstraint(columnNames="email")
})
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, 
allowGetters = true)
public class User {

  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;
  
  @NotBlank
  @Size(min=2, max=20)
  private String username;
  
  @NotBlank
  @Size(min=6, max=120)
  private String password;
  
  @NotBlank
  @Size(min=5, max=50)
  @Email
  private String email;
  
  @NotBlank
  @Size(min=2, max=20)
  private String fullname;
  
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name="user_roles",
    joinColumns = @JoinColumn(name="user_id"),
    inverseJoinColumns = @JoinColumn(name="role_id"))
  private Set<Role> roles = new HashSet<>();
  
  @Column(nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;
  
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;
  
}
