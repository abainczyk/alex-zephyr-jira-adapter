package de.alex.jirazapidemo.jira.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraExecution {
    private Long id;
    private Long cycleId;
    private Long projectId;
    private Long issueId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    @Override
    public String toString() {
        return "JiraExecution{" +
                "id=" + id +
                ", cycleId=" + cycleId +
                ", projectId=" + projectId +
                ", issueId=" + issueId +
                '}';
    }
}
