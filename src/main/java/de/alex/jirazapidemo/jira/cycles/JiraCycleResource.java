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

package de.alex.jirazapidemo.jira.cycles;

import de.alex.jirazapidemo.jira.ExecutionConfig;
import de.alex.jirazapidemo.jira.ExecutionService;
import de.alex.jirazapidemo.jira.JiraEndpoints;
import de.alex.jirazapidemo.jira.JiraResource;
import de.alex.jirazapidemo.jira.entities.CycleExecutionConfig;
import de.alex.jirazapidemo.jira.entities.FolderExecutionConfig;
import de.alex.jirazapidemo.jira.entities.JiraCycleFolder;
import de.alex.jirazapidemo.jira.entities.JiraCycleFolderExecution;
import de.alex.jirazapidemo.jira.entities.JiraExecution;
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
public class JiraCycleResource extends JiraResource {

    private static final String RESOURCE_URL = "/rest/jira/cycles";

    @Autowired
    private JiraEndpoints jiraEndpoints;

    @Autowired
    private ExecutionService executionService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity getCycles(@RequestParam("projectId") String projectId) {
        final Response response = jiraEndpoints.cycles(projectId).get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{cycleId}/folders"
    )
    public ResponseEntity getFolders(@PathVariable Long cycleId,
                                     @RequestParam("projectId") Long projectId,
                                     @RequestParam("versionId") Long versionId) {
        final Response response = client.target(jiraEndpoints.url() + "/zapi/latest/cycle/" + cycleId + "/folders?projectId=" + projectId + "&versionId=" + versionId)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth())
                .get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{cycleId}/folders/{folderId}/tests"
    )
    public ResponseEntity getTestsByFolder(@PathVariable Long cycleId,
                                           @PathVariable Long folderId,
                                           @RequestParam("projectId") Long projectId,
                                           @RequestParam("versionId") Long versionId) {
        final Response response = client.target(jiraEndpoints.url() + "/zapi/latest/execution?cycleId=" + cycleId + "&action=expand&projectId=" + projectId + "&versionId=" + versionId + "&folderId=" + folderId + "&offset=0&sorter=OrderId:ASC")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth())
                .get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/{cycleId}/execute"
    )
    public ResponseEntity executeCycle(@PathVariable Long cycleId,
                                       @RequestBody CycleExecutionConfig config) {

        new Thread(() -> {
            final Response res1 = client.target(jiraEndpoints.url() + "/zapi/latest/cycle/" + cycleId + "/folders?projectId=" + config.getProjectId() + "&versionId=" + config.getVersionId())
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", auth())
                    .get();

            final ArrayList<JiraCycleFolder> folders = res1.readEntity(new GenericType<ArrayList<JiraCycleFolder>>() {
            });

            for (final JiraCycleFolder folder : folders) {
                final Response res2 = client.target(jiraEndpoints.url() + "/zapi/latest/execution?cycleId=" + cycleId + "&action=expand&projectId=" + config.getProjectId() + "&versionId=" + config.getVersionId() + "&folderId=" + folder.getFolderId() + "&offset=0&sorter=OrderId:ASC")
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", auth())
                        .get();

                final List<JiraExecution> executions = res2.readEntity(JiraCycleFolderExecution.class).getExecutions();

                for (JiraExecution execution : executions) {
                    final ExecutionConfig executionConfig = new ExecutionConfig();
                    executionConfig.setJiraProjectId(config.getProjectId());
                    executionConfig.setJiraTestId(execution.getIssueId());
                    executionConfig.setAlexUrlId(config.getUrlId());

                    try {
                        executionService.executeTest(executionConfig, execution);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/{cycleId}/folders/{folderId}/execute"
    )
    public ResponseEntity executeFolder(@PathVariable Long cycleId,
                                        @PathVariable Long folderId,
                                        @RequestBody FolderExecutionConfig config) {

        new Thread(() -> {
            final Response res2 = client.target(jiraEndpoints.url() + "/zapi/latest/execution?cycleId=" + cycleId + "&action=expand&projectId=" + config.getProjectId() + "&versionId=" + config.getVersionId() + "&folderId=" + config.getFolderId() + "&offset=0&sorter=OrderId:ASC")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", auth())
                    .get();

            final List<JiraExecution> executions = res2.readEntity(JiraCycleFolderExecution.class).getExecutions();

            for (JiraExecution execution : executions) {
                final ExecutionConfig executionConfig = new ExecutionConfig();
                executionConfig.setJiraProjectId(config.getProjectId());
                executionConfig.setJiraTestId(execution.getIssueId());
                executionConfig.setAlexUrlId(config.getUrlId());

                try {
                    executionService.executeTest(executionConfig, execution);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return ResponseEntity.ok().build();
    }
}
