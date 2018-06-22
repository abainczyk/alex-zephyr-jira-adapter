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

package de.alex.jirazapidemo.alex;

import de.alex.jirazapidemo.alex.entities.AlexJwt;
import de.alex.jirazapidemo.alex.entities.AlexUserLogin;
import de.alex.jirazapidemo.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to quickly create requests to the REST API of ALEX.
 */
@Service
public class AlexEndpoints {

    private final SettingsService settingsService;

    /** The HTTP Client. */
    private final Client client;

    /** The cached authorization token. Since ALEX keeps auth tokens alive indefinitely, there is no need to refresh it. */
    private String token = null;

    @Autowired
    public AlexEndpoints(SettingsService settingsService) {
        this.settingsService = settingsService;
        this.client = ClientBuilder.newClient();
    }

    /**
     * Authenticate in ALEX.
     *
     * @return The builder for the request.
     */
    public Invocation.Builder login() {
        return client.target(url() + "/users/login")
                .request(MediaType.APPLICATION_JSON);
    }

    /**
     * Endpoint for projects.
     *
     * @return The builder for the request.
     */
    public Invocation.Builder projects() {
        return client.target(url() + "/projects")
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
    public Invocation.Builder project(Long projectId) {
        return client.target(url() + "/projects/" + projectId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for all tests.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder tests(Long projectId) {
        return client.target(url() + "/projects/" + projectId + "/tests")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for all test cases.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder testCases(Long projectId) {
        return client.target(url() + "/projects/" + projectId + "/tests" + "?type=case")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for a single test.
     *
     * @param projectId
     *         The ID of the project.
     * @param testId
     *         The ID of the test.
     * @return The builder for the request.
     */
    public Invocation.Builder test(Long projectId, Long testId) {
        return client.target(url() + "/projects/" + projectId + "/tests/" + testId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for the root test suite.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder rootTest(Long projectId) {
        return client.target(url() + "/projects/" + projectId + "/tests/root")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for executing a test.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder executeTest(Long projectId) {
        return client.target(url() + "/projects/" + projectId + "/tests/execute")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for getting the status of the test process in ALEX.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder testStatus(Long projectId) {
        return client.target(url() + "/projects/" + projectId + "/tests/status")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for getting the latest test report.
     *
     * @param projectId
     *         The ID of the project.
     * @return The builder for the request.
     */
    public Invocation.Builder latestTestReport(Long projectId) {
        return client.target(url() + "/projects/" + projectId + "/tests/reports/latest")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for webhooks.
     *
     * @return The builder for the request.
     */
    public Invocation.Builder webhooks() {
        return client.target(url() + "/webhooks")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Endpoint for handling multiple webhooks.
     *
     * @param webhookIds
     *         The IDs of the webhooks.
     * @return The builder for the request.
     */
    public Invocation.Builder webhooksBatch(List<Long> webhookIds) {
        final String ids = String.join(",", webhookIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList()));

        return client.target(url() + "/webhooks/" + ids)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth());
    }

    /**
     * Get the base URL of ALEX.
     *
     * @return The base URL.
     */
    public String url() {
        return settingsService.getAlexUrl() + "/rest";
    }

    /**
     * Get the "Authorization" header for ALEX.
     *
     * @return The "Authorization" header value.
     */
    public String auth() {
        if (token != null) {
            return token;
        }

        final AlexUserLogin login = new AlexUserLogin(
                settingsService.getAlexEmail(),
                settingsService.getAlexPassword()
        );

        final Response response = login()
                .post(Entity.json(login));

        token = "Bearer " + response.readEntity(AlexJwt.class).getToken();
        return token;
    }
}
