package com.stride.goalnest.service;

import com.stride.goalnest.model.RepoConfig;
import com.stride.goalnest.service.dto.GitHubPRDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GitHubService {

    @Value("${github.token:}")
    private String githubToken;

    private final RestTemplate restTemplate;

    public GitHubService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<GitHubPRDTO> fetchPullRequests(RepoConfig repo, int page) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls?state=closed&sort=updated&direction=desc&per_page=100&page=%d",
                repo.getOwner(), repo.getName(), page);

        return makeRequest(url, new ParameterizedTypeReference<List<GitHubPRDTO>>() {});
    }

    public String fetchReviews(RepoConfig repo, Integer prNumber) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d/reviews",
                repo.getOwner(), repo.getName(), prNumber);
        return makeRequestRaw(url);
    }

    public String fetchComments(RepoConfig repo, Integer prNumber) {
        // Fetching issue comments which cover general conversation on the PR
        String url = String.format("https://api.github.com/repos/%s/%s/issues/%d/comments",
                repo.getOwner(), repo.getName(), prNumber);
        return makeRequestRaw(url);
    }

    public String fetchCommits(RepoConfig repo, Integer prNumber) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d/commits",
                repo.getOwner(), repo.getName(), prNumber);
        return makeRequestRaw(url);
    }

    public String fetchCIStatus(RepoConfig repo, String sha) {
        // Using the combined status endpoint or check-runs.
        // statuses/{sha} returns a list of statuses.
        String url = String.format("https://api.github.com/repos/%s/%s/commits/%s/status",
                repo.getOwner(), repo.getName(), sha);
        return makeRequestRaw(url);
    }

    private <T> T makeRequest(String url, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (githubToken != null && !githubToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + githubToken);
        }

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType);
            handleRateLimit(response.getHeaders());
            return response.getBody();
        } catch (HttpClientErrorException.Forbidden e) {
            if (e.getResponseHeaders() != null && isRateLimited(e.getResponseHeaders())) {
                handleRateLimit(e.getResponseHeaders());
                // Retry once after sleeping
                return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType).getBody();
            }
            throw e;
        }
    }

    private String makeRequestRaw(String url) {
        HttpHeaders headers = new HttpHeaders();
        if (githubToken != null && !githubToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + githubToken);
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            handleRateLimit(response.getHeaders());
            return response.getBody();
        } catch (HttpClientErrorException.Forbidden e) {
             if (e.getResponseHeaders() != null && isRateLimited(e.getResponseHeaders())) {
                handleRateLimit(e.getResponseHeaders());
                // Retry once after sleeping
                return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
            }
            throw e;
        }
    }

    private boolean isRateLimited(HttpHeaders headers) {
        String remaining = headers.getFirst("X-RateLimit-Remaining");
        return remaining != null && Integer.parseInt(remaining) == 0;
    }

    private void handleRateLimit(HttpHeaders headers) {
        String remaining = headers.getFirst("X-RateLimit-Remaining");
        String reset = headers.getFirst("X-RateLimit-Reset");

        if (remaining != null && reset != null) {
            int remainingRequests = Integer.parseInt(remaining);
            if (remainingRequests < 5) { // Threshold
                long resetTime = Long.parseLong(reset);
                long sleepMillis = (resetTime * 1000) - System.currentTimeMillis();
                if (sleepMillis > 0) {
                    try {
                        // Add a small buffer
                        Thread.sleep(sleepMillis + 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
