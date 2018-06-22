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

import de.alex.jirazapidemo.services.SettingsService;
import org.glassfish.jersey.internal.util.Base64;
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

    private final SettingsService settingsService;

    /** The HTTP client. */
    protected final Client client;

    @Autowired
    public JiraEndpoints(SettingsService settingsService) {
        this.settingsService = settingsService;
        this.client = ClientBuilder.newClient();
    }

    /**
     * Endpoint for cycles.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder cycles(String projectId) {
        return client.target(url() + "/zapi/latest/cycle?projectId=" + projectId + "&expand=executionSummaries")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    public Invocation.Builder folders(Long projectId, Long versionId, Long cycleId) {
        return client.target(url() + "/zapi/latest/cycle/" + cycleId + "/folders?projectId=" + projectId + "&versionId=" + versionId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    public Invocation.Builder testsByFolder(Long projectId, Long versionId, Long cycleId, Long folderId) {
        return client.target(url() + "/zapi/latest/execution?cycleId=" + cycleId + "&action=expand&projectId=" + projectId + "&versionId=" + versionId + "&folderId=" + folderId + "&offset=0&sorter=OrderId:ASC")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for a single issue.
     *
     * @param issueId
     *         The ID of the issue.
     * @return The builder for the request.
     */
    public Invocation.Builder issue(Long issueId) {
        return client.target(url() + "/api/2/issue/" + issueId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for projects.
     *
     * @return The builder for the request.
     */
    public Invocation.Builder projects() {
        return client.target(url() + "/api/2/project")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for a single project.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder project(String projectId) {
        return client.target(url() + "/api/2/project/" + projectId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for a test execution.
     *
     * @return The builder for the request.
     */
    public Invocation.Builder execution() {
        return client.target(url() + "/zapi/latest/execution")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for tests.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder tests(Long projectId) {
        try {
            return client.target(url() + "/api/2/search?jql=" + URLEncoder.encode("project = " + projectId + " AND issuetype = Test", "UTF-8"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", auth());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Invocation.Builder versions(String projectId) {
        return client.target(url() + "/api/2/project/" + projectId + "/versions")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", auth());
    }

    public Invocation.Builder testSteps(Long testId) {
        return client.target(url() + "/zapi/latest/teststep/" + testId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", auth());
    }

    public Invocation.Builder testStep(Long testId, Long stepId) {
        return client.target(url() + "/zapi/latest/teststep/" + testId + "/" + stepId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", auth());
    }

    public Invocation.Builder stepResults(Long executionId) {
        return client.target(url() + "/zapi/latest/stepResult?executionId=" + executionId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", auth());
    }

    public Invocation.Builder stepResult(Long stepResultId) {
        return client.target(url() + "/zapi/latest/stepResult/" + stepResultId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", auth());
    }

    /**
     * Get the base URL of the Jira REST API.
     *
     * @return The base URL of the Jira REST API.
     */
    public String url() {
        return settingsService.getJiraUrl() + "/rest";
    }

    /**
     * Returns the content for the HTTP "Authorization" header for HTTP Basic auth "username:password".
     *
     * @return The base64 encoded credentials.
     */
    public String auth() {
        final String credentials = settingsService.getJiraUsername() + ":" + settingsService.getJiraPassword();

        return "Basic " + Base64.encodeAsString(credentials.getBytes());
    }
}
