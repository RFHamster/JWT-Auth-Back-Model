package com.rfhamster.project.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rfhamster.project.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
	@Query("SELECT u FROM User u WHERE u.username =:username")
	User findByUsername(@Param("username") String username);
	
	@Query("SELECT u FROM User u WHERE u.username =:username")
	Optional<User> findByUsernameOP(@Param("username") String username);
}
