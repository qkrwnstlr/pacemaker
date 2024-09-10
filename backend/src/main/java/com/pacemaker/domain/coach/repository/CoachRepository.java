package com.pacemaker.domain.coach.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pacemaker.domain.coach.entity.Coach;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
}
