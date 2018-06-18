/*
 * Copyright 2018 Alexander Bainczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
