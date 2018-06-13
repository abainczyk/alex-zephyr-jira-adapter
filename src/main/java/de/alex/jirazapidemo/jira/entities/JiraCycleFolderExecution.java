package de.alex.jirazapidemo.jira.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraCycleFolderExecution {
    private List<JiraExecution> executions;

    public List<JiraExecution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<JiraExecution> executions) {
        this.executions = executions;
    }
}
