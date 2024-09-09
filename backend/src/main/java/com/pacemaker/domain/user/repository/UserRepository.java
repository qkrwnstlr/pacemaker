package com.pacemaker.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUid(String uid);
	Optional<User> findByUid(String uid);
}
