package com.stride.goalnest.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public class GitHubPRDTO {
    private Long id;
    private Integer number;
    private String title;

    @JsonProperty("user")
    private GitHubUserDTO user;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("merged_at")
    private OffsetDateTime mergedAt;

    private GitHubHeadDTO head;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GitHubUserDTO getUser() {
        return user;
    }

    public void setUser(GitHubUserDTO user) {
        this.user = user;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(OffsetDateTime mergedAt) {
        this.mergedAt = mergedAt;
    }

    public GitHubHeadDTO getHead() {
        return head;
    }

    public void setHead(GitHubHeadDTO head) {
        this.head = head;
    }
}
