package com.stride.goalnest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stride.goalnest.model.IngestedPullRequest;
import com.stride.goalnest.model.RepoConfig;
import com.stride.goalnest.repository.IngestedPullRequestRepository;
import com.stride.goalnest.repository.RepoConfigRepository;
import com.stride.goalnest.service.dto.GitHubPRDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class IngestionJob {

    private static final Logger log = LoggerFactory.getLogger(IngestionJob.class);

    private final RepoConfigRepository repoConfigRepository;
    private final IngestedPullRequestRepository ingestedPullRequestRepository;
    private final GitHubService gitHubService;
    private final ObjectMapper objectMapper;

    public IngestionJob(RepoConfigRepository repoConfigRepository,
                        IngestedPullRequestRepository ingestedPullRequestRepository,
                        GitHubService gitHubService,
                        ObjectMapper objectMapper) {
        this.repoConfigRepository = repoConfigRepository;
        this.ingestedPullRequestRepository = ingestedPullRequestRepository;
        this.gitHubService = gitHubService;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 0 1 * * ?") // Daily at 1 AM
    public void runIngestionJob() {
        log.info("Starting GitHub Ingestion Job");
        List<RepoConfig> repos = repoConfigRepository.findAllEnabled();

        for (RepoConfig repo : repos) {
            try {
                ingestRepo(repo);
            } catch (Exception e) {
                log.error("Failed to ingest repo: " + repo.getName(), e);
                // Continue to next repo
            }
        }
        log.info("Completed GitHub Ingestion Job");
    }

    public void ingestRepo(RepoConfig repo) {
        log.info("Ingesting repo: {}/{}", repo.getOwner(), repo.getName());
        OffsetDateTime watermark = repo.getWatermark();
        // If watermark is null, start from beginning (or a default past date, here assuming null means fetch all)
        // But logic says: updated_at < watermark check. If watermark is null, we can't compare.
        // We should treat null watermark as "very old date" or handle it.
        // For simplicity, if null, we assume we fetch everything.

        List<GitHubPRDTO> candidatePRs = new ArrayList<>();
        int page = 1;
        boolean keepFetching = true;

        while (keepFetching) {
            List<GitHubPRDTO> batch = gitHubService.fetchPullRequests(repo, page);
            if (batch == null || batch.isEmpty()) break;

            for (GitHubPRDTO pr : batch) {
                if (watermark != null && pr.getUpdatedAt().isBefore(watermark)) {
                    keepFetching = false;
                    break;
                }

                if (pr.getMergedAt() != null) {
                    if (watermark == null || !pr.getMergedAt().isBefore(watermark)) {
                         candidatePRs.add(pr);
                    }
                }
            }
            page++;
            // Safety break to prevent infinite loops in case of API issues
            if (page > 1000) keepFetching = false;
        }

        // Sort by merged_at ASC to process in chronological order
        candidatePRs.sort(Comparator.comparing(GitHubPRDTO::getMergedAt));

        int savedCount = 0;
        for (GitHubPRDTO pr : candidatePRs) {
            if (ingestedPullRequestRepository.existsByRepoIdAndPrNumber(repo.getId(), pr.getNumber())) {
                continue;
            }

            try {
                saveIngestedPR(repo, pr);

                // Update Watermark
                repo.setWatermark(pr.getMergedAt());
                repoConfigRepository.save(repo);
                savedCount++;
            } catch (Exception e) {
                log.error("Failed to save PR #{} for repo {}", pr.getNumber(), repo.getName(), e);
            }
        }
        log.info("Saved {} new PRs for repo {}/{}", savedCount, repo.getOwner(), repo.getName());
    }

    private void saveIngestedPR(RepoConfig repo, GitHubPRDTO pr) throws JsonProcessingException {
        IngestedPullRequest ingestedPR = new IngestedPullRequest();
        ingestedPR.setRepoId(repo.getId());
        ingestedPR.setPrNumber(pr.getNumber());
        ingestedPR.setGithubId(pr.getId());
        ingestedPR.setTitle(pr.getTitle());
        if (pr.getUser() != null) {
            ingestedPR.setAuthor(pr.getUser().getLogin());
        }
        ingestedPR.setMergedAt(pr.getMergedAt());
        ingestedPR.setRawPrData(objectMapper.writeValueAsString(pr));

        // Fetch details
        ingestedPR.setRawReviewsData(gitHubService.fetchReviews(repo, pr.getNumber()));
        ingestedPR.setRawCommentsData(gitHubService.fetchComments(repo, pr.getNumber()));
        ingestedPR.setRawCommitsData(gitHubService.fetchCommits(repo, pr.getNumber()));
        if (pr.getHead() != null) {
            ingestedPR.setRawCiStatusData(gitHubService.fetchCIStatus(repo, pr.getHead().getSha()));
        }

        ingestedPullRequestRepository.save(ingestedPR);
    }
}
