package com.stride.goalnest.repository;

import com.stride.goalnest.model.IngestedPullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestedPullRequestRepository extends JpaRepository<IngestedPullRequest, Long> {

    boolean existsByRepoIdAndPrNumber(Long repoId, Integer prNumber);
}
