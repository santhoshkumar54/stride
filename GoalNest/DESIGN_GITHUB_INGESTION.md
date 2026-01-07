# GitHub PR Ingestion Design

## 1. Fetch Strategy

To ensure no missed PRs and no duplicates while handling API limits, the job will use a polling strategy based on the `updated` timestamp, filtering for `merged_at`.

### Endpoint
`GET /repos/{owner}/{repo}/pulls`
- **Parameters**: `state=closed`, `sort=updated`, `direction=desc`, `per_page=100`

### Logic
1.  **Iterate Pages**: Fetch PRs starting from page 1.
2.  **Stop Condition**: Stop fetching when a PR's `updated_at` is older than the repository's `watermark`.
    -   Reasoning: Since `merged_at <= updated_at`, if `updated_at < watermark`, then `merged_at` must also be `< watermark`.
3.  **Filter**:
    -   Ignore PRs where `merged_at` is `null`.
    -   Ignore PRs where `merged_at < watermark` (Include `==` to handle identical timestamps).
4.  **Process**:
    -   Collect all valid PRs from the fetched pages.
    -   Sort them by `merged_at` ascending.
    -   For each PR, fetch additional details (reviews, comments, CI status) and save to the database.
    -   **Idempotency Check**: Before saving, check if `repo_id + pr_number` already exists to handle the overlap caused by inclusive watermark.
    -   Update the repository's watermark to the PR's `merged_at` after successful save (or batch save).

## 2. Data Models

### Entities

1.  **`RepoConfig`**
    -   `id`: Long (PK)
    -   `owner`: String
    -   `name`: String
    -   `watermark`: OffsetDateTime (Last ingested `merged_at`)
    -   `enabled`: Boolean

2.  **`IngestedPullRequest`**
    -   `id`: Long (PK)
    -   `repo_id`: Long (FK)
    -   `pr_number`: Integer
    -   `github_id`: Long (Unique Constraint)
    -   `title`: String
    -   `author`: String
    -   `merged_at`: OffsetDateTime
    -   `raw_pr_data`: CLOB (JSON)
    -   `raw_reviews_data`: CLOB (JSON)
    -   `raw_comments_data`: CLOB (JSON)
    -   `raw_commits_data`: CLOB (JSON)
    -   `raw_ci_status_data`: CLOB (JSON)

## 3. Pseudocode for Watermark Logic

```java
@Scheduled(cron = "0 0 1 * * ?") // Daily at 1 AM
public void runIngestionJob() {
    List<RepoConfig> repos = repoConfigRepository.findAllEnabled();

    for (RepoConfig repo : repos) {
        try {
            ingestRepo(repo);
        } catch (Exception e) {
            log.error("Failed to ingest repo: " + repo.getName(), e);
            // Continue to next repo
        }
    }
}

void ingestRepo(RepoConfig repo) {
    OffsetDateTime watermark = repo.getWatermark();
    List<PullRequest> candidatePRs = new ArrayList<>();
    int page = 1;
    boolean keepFetching = true;

    while (keepFetching) {
        // Fetch page of closed PRs sorted by updated desc
        List<GitHubPRDTO> batch = gitHubService.fetchPullRequests(repo, page);
        if (batch.isEmpty()) break;

        for (GitHubPRDTO pr : batch) {
            if (pr.getUpdatedAt().isBefore(watermark)) {
                keepFetching = false;
                break; // Optimization: No need to check further in this sorted list
            }

            if (pr.getMergedAt() != null && !pr.getMergedAt().isBefore(watermark)) {
                candidatePRs.add(pr);
            }
        }
        page++;
    }

    // Sort by merged_at ASC to process in chronological order
    candidatePRs.sort(Comparator.comparing(GitHubPRDTO::getMergedAt));

    for (GitHubPRDTO pr : candidatePRs) {
        // Idempotency Check
        if (ingestedPRRepository.existsByRepoAndPrNumber(repo, pr.getNumber())) {
            continue;
        }

        // Fetch details
        var reviews = gitHubService.fetchReviews(repo, pr.getNumber());
        var comments = gitHubService.fetchComments(repo, pr.getNumber());
        var commits = gitHubService.fetchCommits(repo, pr.getNumber());
        var ciStatus = gitHubService.fetchCIStatus(repo, pr.getHead().getSha());

        // Save
        saveIngestedPR(repo, pr, reviews, comments, commits, ciStatus);

        // Update Watermark
        repo.setWatermark(pr.getMergedAt());
        repoConfigRepository.save(repo);
    }
}
```

## 4. Failure and Retry Handling

-   **API Rate Limits**:
    -   Check `X-RateLimit-Remaining` header on every response.
    -   If remaining is low (e.g., < 5), sleep until `X-RateLimit-Reset`.
    -   Use `403 Forbidden` handling: Check if it's a rate limit error, if so, sleep and retry.

-   **Network Failures**:
    -   Implement exponential backoff for `5xx` errors or `IOException`.
    -   Retry up to 3 times before failing the specific call.

-   **Job Reliability**:
    -   Transactional processing: `saveIngestedPR` and watermark update should ideally be in the same transaction or idempotent.
    -   If the job crashes mid-way, the `watermark` in DB reflects the last successfully ingested PR. The next run will pick up from there.
    -   Duplicate Safety: `IngestedPullRequest` table should have a unique constraint on `github_id` or `repo_id + pr_number` to prevent duplicates if watermark update fails but insert succeeds (though strictly, transaction management handles this).
