package com.stride.goalnest.repository;

import com.stride.goalnest.model.PerformancePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformancePointRepository extends JpaRepository<PerformancePoint, Long> {
}
