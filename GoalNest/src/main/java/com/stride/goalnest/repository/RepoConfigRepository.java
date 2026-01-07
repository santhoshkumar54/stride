package com.stride.goalnest.repository;

import com.stride.goalnest.model.RepoConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoConfigRepository extends JpaRepository<RepoConfig, Long> {

    @Query("SELECT r FROM RepoConfig r WHERE r.enabled = true")
    List<RepoConfig> findAllEnabled();
}
