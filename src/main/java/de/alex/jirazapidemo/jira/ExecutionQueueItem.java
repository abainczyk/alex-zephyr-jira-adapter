package de.alex.jirazapidemo.jira;

import de.alex.jirazapidemo.db.h2.tables.pojos.TestMapping;
import de.alex.jirazapidemo.jira.entities.JiraExecution;

public class ExecutionQueueItem {
    private JiraExecution execution;
    private ExecutionConfig config;
    private TestMapping testMapping;

    public ExecutionQueueItem(JiraExecution execution,
                              ExecutionConfig config,
                              TestMapping testMapping) {
        this.execution = execution;
        this.config = config;
        this.testMapping = testMapping;
    }

    public JiraExecution getExecution() {
        return execution;
    }

    public void setExecution(JiraExecution execution) {
        this.execution = execution;
    }

    public ExecutionConfig getConfig() {
        return config;
    }

    public void setConfig(ExecutionConfig config) {
        this.config = config;
    }

    public TestMapping getTestMapping() {
        return testMapping;
    }

    public void setTestMapping(TestMapping testMapping) {
        this.testMapping = testMapping;
    }
}
