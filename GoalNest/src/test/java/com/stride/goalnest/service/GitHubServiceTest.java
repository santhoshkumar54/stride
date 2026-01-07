package com.stride.goalnest.service;

import com.stride.goalnest.model.RepoConfig;
import com.stride.goalnest.service.dto.GitHubPRDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubServiceTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        gitHubService = new GitHubService(restTemplateBuilder);
    }

    @Test
    void fetchPullRequests_shouldReturnNull_whenRepoNotFound() {
        RepoConfig repo = new RepoConfig("owner", "nonexistent-repo", null, true);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<GitHubPRDTO>>() {})))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

        List<GitHubPRDTO> result = gitHubService.fetchPullRequests(repo, 1);
        assertNull(result, "Should return null on 404");
    }

    @Test
    void fetchReviews_shouldReturnNull_whenRepoNotFound() {
        RepoConfig repo = new RepoConfig("owner", "nonexistent-repo", null, true);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, null, null));

        String result = gitHubService.fetchReviews(repo, 1);
        assertNull(result, "Should return null on 404");
    }
}
