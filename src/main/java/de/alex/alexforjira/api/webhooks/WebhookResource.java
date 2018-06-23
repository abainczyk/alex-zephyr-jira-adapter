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

package de.alex.alexforjira.api.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.alex.alexforjira.api.messages.MessagesService;
import de.alex.alexforjira.api.projectmappings.ProjectMappingService;
import de.alex.alexforjira.api.testmappings.TestMappingService;
import de.alex.alexforjira.db.h2.tables.pojos.IssueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.SimpleDateFormat;

/** Resource that handles incoming request from Jira and ALEX where webhooks are registered. */
@RestController
public class WebhookResource {

    private static final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    private static final String RESOURCE_URL = "/rest/wh";

    private final MessagesService messagesService;

    private final ProjectMappingService projectMappingService;

    private final TestMappingService testMappingService;

    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookResource(final MessagesService messagesService,
                           final ProjectMappingService projectMappingService,
                           final TestMappingService testMappingService) {
        this.messagesService = messagesService;
        this.projectMappingService = projectMappingService;
        this.testMappingService = testMappingService;

        this.objectMapper = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    /**
     * Handle Jira specific project events.
     *
     * @param data
     *         The project event data.
     * @return Always returns an empty response with status 200.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/jira/projects"
    )
    public ResponseEntity onJiraProjectEvent(final @RequestBody String data) {
        log.info("Entering onJiraProjectEvent() with {}", data);

        try {
            final JiraProjectEvent projectEvent = objectMapper.readValue(data, JiraProjectEvent.class);
            switch (projectEvent.getType()) {
                case "PROJECT_DELETED":
                    final Long projectId = projectEvent.getProjectId();
                    projectMappingService.deleteByJiraProjectId(projectId);
                    messagesService.deleteByProjectId(projectId);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("Failed to handle Jira project event properly");
        }

        log.info("Leaving onJiraProjectEvent() with status {}", HttpStatus.OK.value());
        return ResponseEntity.ok().build();
    }

    /**
     * Handle Jira specific issue events.
     *
     * @param data
     *         The issue event data
     * @return Always returns an empty response with status 200.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/jira/issues"
    )
    public ResponseEntity onJiraIssueEvent(final @RequestBody String data) {
        log.info("Entering onJiraIssueEvent() with {}", data);

        try {
            final IssueEvent event = objectMapper.readValue(data, IssueEvent.class);
            messagesService.create(event);

            switch (event.getType()) {
                case "ISSUE_DELETED":
                    testMappingService.deleteByJiraTestId(event.getIssueId());
                    messagesService.deleteByProjectId(event.getProjectId());
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("Failed to handle Jira issue event properly");
        }


        log.info("Leaving onJiraIssueEvent() with status {}", HttpStatus.OK.value());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/alex/projects"
    )
    public ResponseEntity onAlexProjectEvent(final @RequestBody String data) throws Exception {
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
    public ResponseEntity onAlexTestEvent(final @RequestBody String data) throws Exception {
        final JsonNode testEvent = objectMapper.readTree(data);
        final Long testId;
        switch (testEvent.get("eventType").asText()) {
            case "TEST_DELETED":
                testId = testEvent.get("data").asLong();
                testMappingService.deleteByAlexTestId(testId);
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
