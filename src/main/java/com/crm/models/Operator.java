package com.crm.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "operator")
@PrimaryKeyJoinColumn(name = "user_id")
public class Operator extends User {

	private String fullname;

	@Column(name = "is_root")
	private boolean isRoot;

	@OneToMany(mappedBy = "sender")
	private Set<Feedback> feedbacks = new HashSet<Feedback>();
}
