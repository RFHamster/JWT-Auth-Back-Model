package com.rfhamster.project.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rfhamster.project.enums.UserRole;

public class UserSigninVO extends RepresentationModel<UserSigninVO> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long key;
	private String username;
	private UserRole role;
	
	public UserSigninVO() {
		
	}
	
	public UserSigninVO(Long key, String username, UserRole role) {
		super();
		this.key = key;
		this.username = username;
		this.role = role;
	}
	
	@JsonIgnore
	public List<String> getRoles() {
		List<String> roles = new ArrayList<>();
		roles.add("ROLE_USER");
		if(this.role == UserRole.ADMIN) roles.add("ROLE_ADMIN");
		return roles;
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(key, role, username);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserSigninVO other = (UserSigninVO) obj;
		return Objects.equals(key, other.key) && role == other.role && Objects.equals(username, other.username);
	}
	
}
