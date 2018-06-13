package de.alex.jirazapidemo.jira.entities;

public class CycleExecutionConfig {
    private Long projectId;
    private Long cycleId;
    private Long versionId;
    private Long urlId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    @Override
    public String toString() {
        return "CycleExecutionConfig{" +
                "projectId=" + projectId +
                ", cycleId=" + cycleId +
                ", versionId=" + versionId +
                ", urlId=" + urlId +
                '}';
    }
}
