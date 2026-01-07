# Metrics Computation Engine Design

## 1. Overview
The Metrics Computation Engine is designed to process merged Pull Requests stored in the `IngestedPullRequest` table and derive meaningful engineering metrics. The goal is to provide coaching-oriented insights rather than stack-ranking engineers, focusing on process improvement and collaboration quality.

## 2. Architecture
The engine will operate as a scheduled job that runs periodically (e.g., daily) to process new PRs and update rolling aggregations.

### Workflow
1.  **Selection**: Identify `IngestedPullRequest` records that have not yet been processed for metrics.
2.  **Parsing**: Parse the JSON fields (`raw_pr_data`, `raw_reviews_data`, etc.) into usable objects.
3.  **Computation**: Calculate the defined metrics for each PR.
4.  **Persistence**: Store the raw metric values in a `PrMetric` entity.
5.  **Aggregation**: Update `DailyMetricAggregate` and `RollingMetricAggregate` tables.
6.  **Insight Generation**: Analyze trends to produce textual insights.

## 3. Metrics Definitions & Formulae

All metrics are computed for **merged** PRs.

### 3.1 PR Cycle Time
*   **Definition**: The time elapsed from the PR creation to its merge. This represents the lifespan of the review process.
*   **Formula**: `PR.merged_at - PR.created_at`
*   **Data Source**: `raw_pr_data` (fields: `created_at`, `merged_at`)

### 3.2 Review Latency
*   **Definition**: The time taken for the first review action (comment, approval, or change request) to appear after the PR is created.
*   **Formula**: `Min(Review.submitted_at) - PR.created_at`
    *   If no reviews exist, this metric is null or undefined for that PR.
*   **Data Source**: `raw_reviews_data` (array of review objects with `submitted_at`) and `raw_pr_data`.

### 3.3 Approval-to-Merge Delay
*   **Definition**: The time elapsed between the final required approval and the actual merge event. High delay indicates friction in the release process.
*   **Formula**: `PR.merged_at - Max(Review.submitted_at WHERE state='APPROVED')`
    *   Note: This simplifies the logic by assuming the last approval was the gating factor.
*   **Data Source**: `raw_reviews_data` (filter `state == 'APPROVED'`) and `raw_pr_data`.

### 3.4 Commit Rework After Review
*   **Definition**: The volume of work (measured by number of commits) added to the PR after the review process has started.
*   **Formula**: `Count(Commit) WHERE Commit.timestamp > FirstReview.submitted_at`
*   **Data Source**: `raw_commits_data` (array of commits with timestamps) and `raw_reviews_data`.

### 3.5 Test Inclusion Signals
*   **Definition**: A binary or weighted signal indicating if the PR includes changes to test files.
*   **Formula**:
    *   `1` (True) if `Any(File.path matches pattern)`
    *   Patterns: `*Test.java`, `*/src/test/*`, `*.spec.ts`, etc.
*   **Data Source**: `raw_pr_data` (if file list available) or `raw_commits_data` (if file list available per commit).
    *   *Fallback*: If file names are not explicitly available in ingested JSON, rely on `additions`/`deletions` if `raw_pr_data` provides a breakdown, or note this as a limitation requiring `files` ingestion.

### 3.6 Collaboration Quality Indicators
*   **Definition**: Heuristics measuring the depth of engagement on a PR.
*   **Formulae**:
    *   **Reviewer Count**: `Count(Distinct Reviewers)`
    *   **Comment Depth**: `Count(Comments) / (Additions + Deletions)` (Comments per line of change)
*   **Data Source**: `raw_comments_data`, `raw_reviews_data`, `raw_pr_data`.

## 4. Aggregation Approach

### 4.1 Daily Aggregation
*   **Granularity**: Per Repository, Per Day.
*   **Method**: Calculate the Mean, Median, and 95th Percentile for each time-based metric for all PRs merged on that day.
*   **Storage**: `DailyMetricAggregate` table.

### 4.2 Rolling Aggregation
*   **Windows**: 7-day and 30-day rolling windows.
*   **Method**: Re-compute aggregates based on the daily buckets.
*   **Purpose**: To smooth out daily volatility and show trends.

## 5. Coaching-Oriented Insights

The system will generate insights based on trends rather than absolute values to encourage growth.

### Output Logic
*   **Compare**: Current 7-day rolling average vs. Previous 7-day rolling average.
*   **Thresholds**: Define improvement/degradation thresholds (e.g., >10% change).

### Example Insights
*   **Cycle Time**: "Cycle time has increased by 15% this week. Consider breaking down larger tasks into smaller PRs to speed up flow."
*   **Review Latency**: "Review latency is trending down! The team is being very responsive."
*   **Rework**: "High rework detected in recent PRs. It might be helpful to discuss requirements or design before implementation begins."
*   **Tests**: "Test inclusion rate is below 50%. Remember to include unit tests with new features."
