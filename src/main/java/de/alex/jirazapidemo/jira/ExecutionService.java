package de.alex.jirazapidemo.jira;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.alex.jirazapidemo.alex.AlexEndpoints;
import de.alex.jirazapidemo.api.settings.SettingsService;
import de.alex.jirazapidemo.api.testmappings.TestMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.TestMapping;
import de.alex.jirazapidemo.jira.entities.JiraExecution;
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

    @Autowired
    private JiraResource jiraResource;

    @Autowired
    private JiraEndpoints jiraEndpoints;

    @Autowired
    private AlexEndpoints alexEndpoints;

    @Autowired
    private TestMappingService testMappingService;

    @Autowired
    private SettingsService settingsService;

    private final Client client = ClientBuilder.newClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean active = false;

    private BlockingDeque<ExecutionQueueItem> executionQueue = new LinkedBlockingDeque<>();

    public void executeTest(ExecutionConfig config) throws Exception {
        checkIfTestMappingExists(config.getJiraTestId());
        final TestMapping testMapping = testMappingService.findOneByJiraProjectIdAndJiraTestId(
                config.getJiraProjectId(), config.getJiraTestId());

        final JiraExecution execution = createExecutionInJira(config.getJiraProjectId(), config.getJiraTestId());

        executionQueue.offerLast(new ExecutionQueueItem(execution, config, testMapping));
        start();
    }

    public ExecutionStatus getStatus() {
        return new ExecutionStatus(active, executionQueue.size());
    }

    public void executeTest(ExecutionConfig config, JiraExecution execution) throws Exception {
        checkIfTestMappingExists(config.getJiraTestId());
        final TestMapping testMapping = testMappingService.findOneByJiraProjectIdAndJiraTestId(
                config.getJiraProjectId(), config.getJiraTestId());

        executionQueue.offerLast(new ExecutionQueueItem(execution, config, testMapping));
        start();
    }

    public void start() {
        if (!active) {
            waitForTestToFinish();
        }
    }

    private void checkIfTestMappingExists(Long testId) throws Exception {
        if (!testMappingService.existsByIssueId(testId)) {
            throw new Exception("The mapping for the test has not been defined yet.");
        }
    }

    private JiraExecution createExecutionInJira(Long projectId, Long testId) throws Exception {
        final String payload = "{" +
                "\"cycleId\":\"-1\"," +
                "\"issueId\":\"" + testId + "\"," +
                "\"projectId\":\"" + projectId + "\"," +
                "\"versionId\":\"-1\"" +
                "}";

        final Response response = jiraEndpoints.execution().post(Entity.json(payload));

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            throw new Exception("The execution could not be created in Jira.");
        }

        final String executionMap = response.readEntity(String.class);
        final JsonNode executionNode = objectMapper.readTree(executionMap).fields().next().getValue();
        return objectMapper.readValue(executionNode.toString(), JiraExecution.class);
    }

    private void executeTestInAlex(ExecutionConfig config, TestMapping testMapping) throws Exception {
        final String testConfig = "{" +
                "\"tests\":[" + testMapping.getAlexTestId() + "]" +
                ",\"createReport\": true" +
                ",\"url\": " + config.getAlexUrlId() +
                ",\"driverConfig\":{" +
                "   \"width\":1280" +
                "   ,\"height\":1024" +
                "   ,\"implicitlyWait\":0" +
                "   ,\"pageLoadTimeout\":10" +
                "   ,\"scriptTimeout\":10" +
                "   ,\"name\":\"firefox\"" +
                "   ,\"headless\":false" +
                "   ,\"xvfbPort\":null" +
                "}}";

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

                        final String reportUrl = "Report: " + settingsService.getAlexSettings().getUri() + "/#!/redirect?to=" + URLEncoder.encode("/projects/" + testMapping.getAlexProjectId() + "/tests/reports/" + reportJson.get("id").asText(), "UTF-8");
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
        client.target(jiraEndpoints.uri() + "/zapi/latest/execution/" + execution.getId() + "/execute")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraResource.auth())
                .put(Entity.json(execution));
    }
}
