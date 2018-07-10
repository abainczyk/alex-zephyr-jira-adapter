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

package de.alex.alexforjira.api.jira.cycles;

import de.alex.alexforjira.api.executions.ExecutionService;
import de.alex.alexforjira.api.executions.entities.ExecutionConfig;
import de.alex.alexforjira.api.jira.JiraEndpoints;
import de.alex.alexforjira.api.jira.entities.JiraCycleFolder;
import de.alex.alexforjira.api.jira.entities.JiraCycleFolderExecution;
import de.alex.alexforjira.api.jira.entities.JiraExecution;
import de.alex.alexforjira.security.ProjectForbiddenException;
import de.alex.alexforjira.shared.JiraUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@RestController
public class JiraCycleResource {

    private static final String RESOURCE_URL = "/rest/jira/cycles";

    private final JiraEndpoints jiraEndpoints;

    private final JiraUtils jiraUtils;

    private final ExecutionService executionService;

    @Autowired
    public JiraCycleResource(final JiraEndpoints jiraEndpoints,
                             final JiraUtils jiraUtils,
                             final ExecutionService executionService) {
        this.jiraEndpoints = jiraEndpoints;
        this.jiraUtils = jiraUtils;
        this.executionService = executionService;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity getCycles(final @RequestParam("projectId") Long projectId)
            throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(projectId);

        final Response response = jiraEndpoints.cycles(projectId).get();
        return ResponseEntity.status(response.getStatus()).body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{cycleId}/folders",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity getFolders(final @PathVariable Long cycleId,
                                     final @RequestParam("projectId") Long projectId,
                                     final @RequestParam("versionId") Long versionId)
            throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(projectId);

        final Response response = jiraEndpoints.folders(projectId, versionId, cycleId).get();
        return ResponseEntity.status(response.getStatus()).body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{cycleId}/folders/{folderId}/tests",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity getTestsByFolder(final @PathVariable Long cycleId,
                                           final @PathVariable Long folderId,
                                           final @RequestParam("projectId") Long projectId,
                                           final @RequestParam("versionId") Long versionId)
            throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(projectId);

        final Response response = jiraEndpoints.testsByFolder(projectId, versionId, cycleId, folderId).get();
        return ResponseEntity.status(response.getStatus()).body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/{cycleId}/execute"
    )
    public ResponseEntity executeCycle(final @PathVariable Long cycleId,
                                       final @RequestBody CycleExecutionConfig config)
            throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(config.getProjectId());

        new Thread(() -> {
            final Response res1 = jiraEndpoints.folders(config.getProjectId(), config.getVersionId(), cycleId).get();
            final ArrayList<JiraCycleFolder> folders = res1.readEntity(new GenericType<ArrayList<JiraCycleFolder>>() {
            });

            for (final JiraCycleFolder folder : folders) {
                final Response res2 = jiraEndpoints.testsByFolder(config.getProjectId(), config.getVersionId(), cycleId, folder.getFolderId()).get();
                final List<JiraExecution> executions = res2.readEntity(JiraCycleFolderExecution.class).getExecutions();
                executeTests(executions, config);
            }
        }).start();

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/{cycleId}/folders/{folderId}/execute"
    )
    public ResponseEntity executeFolder(final @PathVariable Long cycleId,
                                        final @PathVariable Long folderId,
                                        final @RequestBody CycleFolderExecutionConfig config)
            throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(config.getProjectId());

        new Thread(() -> {
            final Response res = jiraEndpoints.testsByFolder(config.getProjectId(), config.getVersionId(), cycleId, folderId).get();
            final List<JiraExecution> executions = res.readEntity(JiraCycleFolderExecution.class).getExecutions();
            executeTests(executions, config);
        }).start();

        return ResponseEntity.ok().build();
    }

    private void executeTests(List<JiraExecution> executions, CycleExecutionConfig config) {
        for (JiraExecution execution : executions) {
            final ExecutionConfig executionConfig = new ExecutionConfig();
            executionConfig.setJiraProjectId(config.getProjectId());
            executionConfig.setJiraTestId(execution.getIssueId());
            executionConfig.setAlexUrlId(config.getAlexUrlId());

            try {
                executionService.executeTest(executionConfig, execution);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
