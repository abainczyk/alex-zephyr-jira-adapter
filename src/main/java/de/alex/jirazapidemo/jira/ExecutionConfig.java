package de.alex.jirazapidemo.jira;

public class ExecutionConfig {

    private Long jiraProjectId;

    private Long alexUrlId;

    private Long jiraTestId;

    public Long getAlexUrlId() {
        return alexUrlId;
    }

    public void setAlexUrlId(Long alexUrlId) {
        this.alexUrlId = alexUrlId;
    }

    public Long getJiraProjectId() {
        return jiraProjectId;
    }

    public void setJiraProjectId(Long jiraProjectId) {
        this.jiraProjectId = jiraProjectId;
    }

    public Long getJiraTestId() {
        return jiraTestId;
    }

    public void setJiraTestId(Long jiraTestId) {
        this.jiraTestId = jiraTestId;
    }
}
