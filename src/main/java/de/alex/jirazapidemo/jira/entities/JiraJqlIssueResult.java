package de.alex.jirazapidemo.jira.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraJqlIssueResult {

    private List<JiraIssue> issues;

    public List<JiraIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<JiraIssue> issues) {
        this.issues = issues;
    }

}
