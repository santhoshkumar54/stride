package com.stride.goalnest.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ingested_pull_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"github_id"})
})
public class IngestedPullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repo_id")
    private Long repoId;

    @Column(name = "pr_number")
    private Integer prNumber;

    @Column(name = "github_id", nullable = false, unique = true)
    private Long githubId;

    private String title;
    private String author;

    @Column(name = "merged_at")
    private OffsetDateTime mergedAt;

    @Lob
    @Column(name = "raw_pr_data")
    private String rawPrData;

    @Lob
    @Column(name = "raw_reviews_data")
    private String rawReviewsData;

    @Lob
    @Column(name = "raw_comments_data")
    private String rawCommentsData;

    @Lob
    @Column(name = "raw_commits_data")
    private String rawCommitsData;

    @Lob
    @Column(name = "raw_ci_status_data")
    private String rawCiStatusData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
    }

    public Integer getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(Integer prNumber) {
        this.prNumber = prNumber;
    }

    public Long getGithubId() {
        return githubId;
    }

    public void setGithubId(Long githubId) {
        this.githubId = githubId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public OffsetDateTime getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(OffsetDateTime mergedAt) {
        this.mergedAt = mergedAt;
    }

    public String getRawPrData() {
        return rawPrData;
    }

    public void setRawPrData(String rawPrData) {
        this.rawPrData = rawPrData;
    }

    public String getRawReviewsData() {
        return rawReviewsData;
    }

    public void setRawReviewsData(String rawReviewsData) {
        this.rawReviewsData = rawReviewsData;
    }

    public String getRawCommentsData() {
        return rawCommentsData;
    }

    public void setRawCommentsData(String rawCommentsData) {
        this.rawCommentsData = rawCommentsData;
    }

    public String getRawCommitsData() {
        return rawCommitsData;
    }

    public void setRawCommitsData(String rawCommitsData) {
        this.rawCommitsData = rawCommitsData;
    }

    public String getRawCiStatusData() {
        return rawCiStatusData;
    }

    public void setRawCiStatusData(String rawCiStatusData) {
        this.rawCiStatusData = rawCiStatusData;
    }
}
