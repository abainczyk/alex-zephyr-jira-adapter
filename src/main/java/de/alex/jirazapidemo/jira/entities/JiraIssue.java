package de.alex.jirazapidemo.jira.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssue {

    private Long id;

    private JiraIssueFields fields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JiraIssueFields getFields() {
        return fields;
    }

    public void setFields(JiraIssueFields fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "JiraIssue{" +
                "id='" + id + '\'' +
                ", fields=" + fields +
                '}';
    }
}
