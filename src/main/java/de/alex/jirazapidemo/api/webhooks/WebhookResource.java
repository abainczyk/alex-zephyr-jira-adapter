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

package de.alex.jirazapidemo.api.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.alex.jirazapidemo.api.events.IssueEventService;
import de.alex.jirazapidemo.api.projectmappings.ProjectMappingService;
import de.alex.jirazapidemo.api.testmappings.TestMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.IssueEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
public class WebhookResource {

    private final String RESOURCE_URL = "/rest/wh";

    private IssueEventService issueEventService;

    private ProjectMappingService projectMappingService;

    private TestMappingService testMappingService;

    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookResource(IssueEventService issueEventService,
                           ProjectMappingService projectMappingService,
                           TestMappingService testMappingService) {
        this.issueEventService = issueEventService;
        this.projectMappingService = projectMappingService;
        this.testMappingService = testMappingService;

        this.objectMapper = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/jira/projects"
    )
    public ResponseEntity handleJiraProjects(final @RequestBody String data) throws Exception {
        final JsonNode projectEvent = objectMapper.readTree(data);
        switch (projectEvent.get("type").asText()) {
            case "PROJECT_DELETED":
                final Long projectId = projectEvent.get("projectId").asLong();
                projectMappingService.deleteByJiraProjectId(projectId);
                issueEventService.deleteByProjectId(projectId);
                break;
            default:
                break;
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/jira/issues"
    )
    public ResponseEntity handleJiraIssues(final @RequestBody String data) throws Exception {
        final IssueEvent event = objectMapper.readValue(data, IssueEvent.class);
        issueEventService.create(event);

        switch (event.getType()) {
            case "ISSUE_DELETED":
                testMappingService.deleteAllByJiraTestId(event.getIssueId());
                issueEventService.deleteByProjectId(event.getProjectId());
                break;
            default:
                break;
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/alex/projects"
    )
    public ResponseEntity handleAlexProjects(final @RequestBody String data) throws Exception {
        final JsonNode projectEvent = objectMapper.readTree(data);
        switch (projectEvent.get("eventType").asText()) {
            case "PROJECT_DELETED":
                final Long projectId = projectEvent.get("data").asLong();
                testMappingService.deleteAllByAlexProjectId(projectId);
                projectMappingService.deleteByAlexProjectId(projectId);
                break;
            default:
                break;
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/alex/tests"
    )
    public ResponseEntity handleAlexTests(final @RequestBody String data) throws Exception {
        final JsonNode testEvent = objectMapper.readTree(data);
        final Long testId;
        switch (testEvent.get("eventType").asText()) {
            case "TEST_DELETED":
                testId = testEvent.get("data").asLong();
                testMappingService.deleteAllByAlexTestId(testId);
                break;
            case "TEST_UPDATED":
                testId = testEvent.get("data").get("id").asLong();
                testMappingService.incrementTestUpdates(testId);
                break;
            default:
                break;
        }

        return ResponseEntity.ok().build();
    }
}
