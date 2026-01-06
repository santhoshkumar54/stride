package com.stride.goalnest.repository;

import com.stride.goalnest.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
