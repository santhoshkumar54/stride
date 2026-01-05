package com.stride.goalnest.repository;

import com.stride.goalnest.model.RoleRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRatingRepository extends JpaRepository<RoleRating, Long> {
}
