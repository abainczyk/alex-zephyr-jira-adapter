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

package de.alex.alexforjira.api.executions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.alex.alexforjira.api.alex.AlexEndpoints;
import de.alex.alexforjira.api.executions.entities.Execution;
import de.alex.alexforjira.api.executions.entities.ExecutionConfig;
import de.alex.alexforjira.api.executions.entities.ExecutionQueueItem;
import de.alex.alexforjira.api.executions.entities.ExecutionStatus;
import de.alex.alexforjira.api.executions.entities.StepResult;
import de.alex.alexforjira.api.jira.JiraEndpoints;
import de.alex.alexforjira.api.jira.entities.JiraExecution;
import de.alex.alexforjira.api.testmappings.TestMappingService;
import de.alex.alexforjira.db.h2.tables.pojos.TestMapping;
import de.alex.alexforjira.shared.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class ExecutionService {

    private final JiraEndpoints jiraEndpoints;

    private final AlexEndpoints alexEndpoints;

    private final TestMappingService testMappingService;

    private final SettingsService settingsService;

    private final Client client = ClientBuilder.newClient();

    private final ObjectMapper objectMapper;

    /** The queue of tests to execute. */
    private final BlockingDeque<ExecutionQueueItem> executionQueue;

    /** If the test execution is in progress. */
    private boolean active;

    @Autowired
    public ExecutionService(JiraEndpoints jiraEndpoints,
                            AlexEndpoints alexEndpoints,
                            TestMappingService testMappingService,
                            SettingsService settingsService) {
        this.jiraEndpoints = jiraEndpoints;
        this.alexEndpoints = alexEndpoints;
        this.testMappingService = testMappingService;
        this.settingsService = settingsService;

        this.objectMapper = new ObjectMapper();
        this.executionQueue = new LinkedBlockingDeque<>();
        this.active = false;
    }

    public void executeTest(ExecutionConfig config) throws Exception {
        checkIfTestMappingExists(config.getJiraTestId());
        final TestMapping testMapping = testMappingService.findOneByJiraTestId(config.getJiraTestId());
        final JiraExecution execution = createExecutionInJira(config.getJiraProjectId(), config.getJiraTestId());

        executionQueue.offerLast(new ExecutionQueueItem(execution, config, testMapping));
        start();
    }

    public ExecutionStatus getStatus() {
        return new ExecutionStatus(active, executionQueue.size());
    }

    public void executeTest(ExecutionConfig config, JiraExecution execution) throws Exception {
        checkIfTestMappingExists(config.getJiraTestId());
        final TestMapping testMapping = testMappingService.findOneByJiraTestId(config.getJiraTestId());

        executionQueue.offerLast(new ExecutionQueueItem(execution, config, testMapping));
        start();
    }

    public void start() {
        if (!active) {
            waitForTestToFinish();
        }
    }

    private void checkIfTestMappingExists(Long testId) throws Exception {
        if (!testMappingService.existsByTestId(testId)) {
            throw new Exception("The mapping for the test has not been defined yet.");
        }
    }

    private JiraExecution createExecutionInJira(Long projectId, Long testId) throws Exception {
        final String payload = "{"
                + "\"cycleId\":\"-1\","
                + "\"issueId\":\"" + testId + "\","
                + "\"projectId\":\"" + projectId + "\","
                + "\"versionId\":\"-1\"" // -1 means execute test not in cycle
                + "}";

        final Response response = jiraEndpoints.execution().post(Entity.json(payload));

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new Exception("The execution could not be created in Jira.");
        }

        final String executionMap = response.readEntity(String.class);
        final JsonNode executionNode = objectMapper.readTree(executionMap).fields().next().getValue();
        return objectMapper.readValue(executionNode.toString(), JiraExecution.class);
    }

    private void executeTestInAlex(ExecutionConfig config, TestMapping testMapping) throws Exception {
        final String testConfig = "{"
                + "\"tests\":[" + testMapping.getAlexTestId() + "]"
                + ",\"createReport\": true"
                + ",\"url\": " + config.getAlexUrlId()
                + ",\"driverConfig\":{"
                + "   \"width\":" + settingsService.getBrowserWidth()
                + "   ,\"height\":" + settingsService.getBrowserHeight()
                + "   ,\"implicitlyWait\":0"
                + "   ,\"pageLoadTimeout\":10"
                + "   ,\"scriptTimeout\":10"
                + "   ,\"name\":\"" + settingsService.getBrowserName() + "\""
                + "   ,\"headless\":" + settingsService.getBrowserHeadless()
                + "   ,\"xvfbPort\":null"
                + "}}";

        final Response response = alexEndpoints.executeTest(testMapping.getAlexProjectId())
                .post(Entity.json(testConfig));

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new Exception("The test could not be executed in ALEX.");
        }
    }

    private void waitForTestToFinish() {
        active = true;
        new Thread(() -> {
            while (!executionQueue.isEmpty()) {
                final ExecutionQueueItem queueItem = executionQueue.peek();
                final TestMapping testMapping = queueItem.getTestMapping();
                final JiraExecution execution = queueItem.getExecution();

                try {
                    executeTestInAlex(queueItem.getConfig(), testMapping);

                    while (true) {
                        final Response statusResponse = alexEndpoints.testStatus(testMapping.getAlexProjectId()).get();

                        final JsonNode statusJson = objectMapper.readTree(statusResponse.readEntity(String.class));
                        final boolean active = statusJson.get("active").asBoolean();
                        if (active) {
                            Thread.sleep(3000);
                            continue;
                        }

                        final Response reportResponse = alexEndpoints.latestTestReport(testMapping.getAlexProjectId()).get();

                        final JsonNode reportJson = objectMapper.readTree(reportResponse.readEntity(String.class));

                        final List<StepResult> stepResults = getStepsByExecutionId(execution.getId());

                        final JsonNode outputs = reportJson.get("testResults").get(0).get("outputs");

                        for (int i = 0; i < stepResults.size(); i++) {
                            final boolean passed = outputs.get(i).get("success").asBoolean();
                            final String comment = outputs.get(i).get("output").asText();

                            stepResults.get(i).setStatus(passed ? StepResult.PASSED : StepResult.FAILED);
                            stepResults.get(i).setComment(comment);
                        }

                        updateStepResults(stepResults);

                        final Execution exec = new Execution();
                        exec.setId(execution.getId());
                        exec.setStatus(reportJson.get("passed").asBoolean() ? StepResult.PASSED : StepResult.FAILED);

                        final String reportUrl = "Report: " + settingsService.getAlexUrl() + "/#!/redirect?to=" + URLEncoder.encode("/projects/" + testMapping.getAlexProjectId() + "/tests/reports/" + reportJson.get("id").asText(), "UTF-8");
                        exec.setComment(reportUrl);
                        updateExecution(exec);

                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                } finally {
                    executionQueue.removeFirst();
                }
            }

            active = false;
        }).start();
    }

    public void abort() {
        this.executionQueue.clear();
        this.active = false;
    }

    private List<StepResult> getStepsByExecutionId(Long executionId) {
        final Response response = jiraEndpoints.stepResults(executionId).get();
        return response.readEntity(new GenericType<List<StepResult>>() {
        });
    }

    private void updateStepResults(List<StepResult> results) {
        for (StepResult result : results) {
            jiraEndpoints.stepResult(result.getId()).put(Entity.json(result));
        }
    }

    private void updateExecution(Execution execution) {
        client.target(jiraEndpoints.url() + "/zapi/latest/execution/" + execution.getId() + "/execute")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraEndpoints.auth())
                .put(Entity.json(execution));
    }
}
