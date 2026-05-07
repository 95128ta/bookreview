package com.bookreview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookreview.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

	Optional<AppUser> findByLoginId(String loginId);

	boolean existsByLoginId(String loginId);
}

