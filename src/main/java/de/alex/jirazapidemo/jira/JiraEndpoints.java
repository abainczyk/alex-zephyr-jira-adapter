package de.alex.jirazapidemo.jira;

import de.alex.jirazapidemo.api.settings.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Endpoints for Jira related entities to quickly make requests.
 */
@Service
public class JiraEndpoints {

    @Autowired
    protected SettingsService settingsService;

    @Autowired
    private JiraResource jiraResource;

    /** The HTTP client. */
    protected final Client client = ClientBuilder.newClient();

    /**
     * Endpoint for cycles.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder cycles(String projectId) {
        return client.target(uri() + "/zapi/latest/cycle?projectId=" + projectId + "&expand=executionSummaries")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", jiraResource.auth());
    }

    /**
     * Endpoint for a single issue.
     *
     * @param issueId
     *         The ID of the issue.
     * @return The builder for the request.
     */
    public Invocation.Builder issue(Long issueId) {
        return client.target(uri() + "/api/2/issue/" + issueId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", jiraResource.auth());
    }

    /**
     * Endpoint for projects.
     *
     * @return The builder for the request.
     */
    public Invocation.Builder projects() {
        return client.target(uri() + "/api/2/project")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", jiraResource.auth());
    }

    /**
     * Endpoint for a single project.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder project(String projectId) {
        return client.target(uri() + "/api/2/project/" + projectId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", jiraResource.auth());
    }

    /**
     * Endpoint for a test execution.
     *
     * @return The builder for the request.
     */
    public Invocation.Builder execution() {
        return client.target(uri() + "/zapi/latest/execution")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraResource.auth());
    }

    /**
     * Endpoint for tests.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder tests(String projectId) {
        try {
            return client.target(uri() + "/api/2/search?jql=" + URLEncoder.encode("project = " + projectId + " AND issuetype = Test", "UTF-8"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", jiraResource.auth());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Invocation.Builder versions(String projectId) {
        return client.target(uri() + "/api/2/project/" + projectId + "/versions")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraResource.auth());
    }

    public Invocation.Builder testSteps(Long testId) {
        return client.target(uri() + "/zapi/latest/teststep/" + testId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraResource.auth());
    }

    public Invocation.Builder testStep(Long testId, Long stepId) {
        return client.target(uri() + "/zapi/latest/teststep/" + testId + "/" + stepId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraResource.auth());
    }

    public Invocation.Builder stepResults(Long executionId) {
        return client.target(uri() + "/zapi/latest/stepResult?executionId=" + executionId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraResource.auth());
    }

    public Invocation.Builder stepResult(Long stepResultId) {
        return client.target(uri() + "/zapi/latest/stepResult/" + stepResultId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", jiraResource.auth());
    }

    /**
     * Get the base URL of the Jira REST API.
     *
     * @return The base URL of the Jira REST API.
     */
    public String uri() {
        return settingsService.getJiraSettings().getUri() + "/rest";
    }

}
