package com.stride.goalnest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stride.goalnest.model.IngestedPullRequest;
import com.stride.goalnest.model.RepoConfig;
import com.stride.goalnest.repository.IngestedPullRequestRepository;
import com.stride.goalnest.repository.RepoConfigRepository;
import com.stride.goalnest.service.dto.GitHubHeadDTO;
import com.stride.goalnest.service.dto.GitHubPRDTO;
import com.stride.goalnest.service.dto.GitHubUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionJobTest {

    @Mock
    private RepoConfigRepository repoConfigRepository;

    @Mock
    private IngestedPullRequestRepository ingestedPullRequestRepository;

    @Mock
    private GitHubService gitHubService;

    private IngestionJob ingestionJob;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ingestionJob = new IngestionJob(repoConfigRepository, ingestedPullRequestRepository, gitHubService, objectMapper);
    }

    @Test
    void runIngestionJob_shouldIngestEnabledRepos() {
        RepoConfig repo = new RepoConfig("owner", "repo", null, true);
        repo.setId(1L);
        when(repoConfigRepository.findAllEnabled()).thenReturn(Collections.singletonList(repo));

        // Mock GitHub PRs
        GitHubPRDTO pr1 = createPR(101, "First PR", "2023-01-01T10:00:00Z", "2023-01-01T12:00:00Z");

        when(gitHubService.fetchPullRequests(eq(repo), eq(1))).thenReturn(Collections.singletonList(pr1));
        when(gitHubService.fetchPullRequests(eq(repo), eq(2))).thenReturn(Collections.emptyList()); // End of pages

        when(gitHubService.fetchReviews(any(), anyInt())).thenReturn("[]");
        when(gitHubService.fetchComments(any(), anyInt())).thenReturn("[]");
        when(gitHubService.fetchCommits(any(), anyInt())).thenReturn("[]");
        when(gitHubService.fetchCIStatus(any(), anyString())).thenReturn("{}");

        when(ingestedPullRequestRepository.existsByRepoIdAndPrNumber(eq(1L), eq(101))).thenReturn(false);

        ingestionJob.runIngestionJob();

        verify(ingestedPullRequestRepository, times(1)).save(any(IngestedPullRequest.class));
        verify(repoConfigRepository, times(1)).save(repo);
        assertEquals(pr1.getMergedAt(), repo.getWatermark());
    }

    @Test
    void ingestRepo_shouldStopAtWatermark() {
        OffsetDateTime watermark = OffsetDateTime.parse("2023-01-02T00:00:00Z");
        RepoConfig repo = new RepoConfig("owner", "repo", watermark, true);
        repo.setId(1L);

        // PR1: Updated recently, Merged after watermark -> Should be processed
        GitHubPRDTO pr1 = createPR(102, "New PR", "2023-01-03T10:00:00Z", "2023-01-03T12:00:00Z");

        // PR2: Updated before watermark -> Should stop fetching
        GitHubPRDTO pr2 = createPR(101, "Old PR", "2023-01-01T10:00:00Z", "2023-01-01T12:00:00Z");

        when(gitHubService.fetchPullRequests(eq(repo), eq(1))).thenReturn(List.of(pr1, pr2));

        // Mock details for pr1
        when(gitHubService.fetchReviews(eq(repo), eq(102))).thenReturn("[]");
        when(gitHubService.fetchComments(eq(repo), eq(102))).thenReturn("[]");
        when(gitHubService.fetchCommits(eq(repo), eq(102))).thenReturn("[]");
        when(gitHubService.fetchCIStatus(eq(repo), anyString())).thenReturn("{}");

        ingestionJob.ingestRepo(repo);

        // Should only save pr1
        ArgumentCaptor<IngestedPullRequest> captor = ArgumentCaptor.forClass(IngestedPullRequest.class);
        verify(ingestedPullRequestRepository, times(1)).save(captor.capture());

        assertEquals(102, captor.getValue().getPrNumber());
        assertEquals(pr1.getMergedAt(), repo.getWatermark());

        // Verify we didn't fetch details for pr2
        verify(gitHubService, never()).fetchReviews(eq(repo), eq(101));
    }

    private GitHubPRDTO createPR(int number, String title, String updatedAt, String mergedAt) {
        GitHubPRDTO pr = new GitHubPRDTO();
        pr.setId((long) number);
        pr.setNumber(number);
        pr.setTitle(title);
        pr.setUpdatedAt(OffsetDateTime.parse(updatedAt));
        if (mergedAt != null) {
            pr.setMergedAt(OffsetDateTime.parse(mergedAt));
        }
        GitHubUserDTO user = new GitHubUserDTO();
        user.setLogin("user" + number);
        pr.setUser(user);

        GitHubHeadDTO head = new GitHubHeadDTO();
        head.setSha("sha" + number);
        pr.setHead(head);

        return pr;
    }
}
