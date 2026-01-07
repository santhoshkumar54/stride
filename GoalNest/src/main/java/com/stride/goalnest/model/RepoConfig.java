package com.stride.goalnest.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "repo_config")
public class RepoConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;
    private String name;

    private OffsetDateTime watermark;

    private Boolean enabled;

    public RepoConfig() {
    }

    public RepoConfig(String owner, String name, OffsetDateTime watermark, Boolean enabled) {
        this.owner = owner;
        this.name = name;
        this.watermark = watermark;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getWatermark() {
        return watermark;
    }

    public void setWatermark(OffsetDateTime watermark) {
        this.watermark = watermark;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
