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

package de.alex.alexforjira.api.messages;

import de.alex.alexforjira.api.projectmappings.ProjectMappingResource;
import de.alex.alexforjira.db.h2.tables.pojos.IssueEvent;
import de.alex.alexforjira.security.ProjectForbiddenException;
import de.alex.alexforjira.shared.JiraUtils;
import de.alex.alexforjira.shared.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Endpoints for handling events.
 */
@RestController
public class MessagesResource {

    private static final Logger log = LoggerFactory.getLogger(ProjectMappingResource.class);

    private static final String RESOURCE_URL = "/rest/messages/projects/{projectId}/issues";

    private final MessagesService issueEventService;

    private final JiraUtils jiraUtils;

    @Autowired
    public MessagesResource(final MessagesService issueEventService, final JiraUtils jiraUtils) {
        this.issueEventService = issueEventService;
        this.jiraUtils = jiraUtils;
    }

    /**
     * Get all events related to a Jira project.
     *
     * @param projectId
     *         The ID of the Jira project.
     * @return All project related events.
     * @throws ProjectForbiddenException
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL,
            produces = MediaType.APPLICATION_JSON
    )
    private ResponseEntity<List<IssueEvent>> getAll(@PathVariable("projectId") Long projectId)
            throws ProjectForbiddenException {
        log.info("Entering getAll(projectId: {})", projectId);
        jiraUtils.checkIfProjectIsAllowed(projectId);
        log.info("Leaving getAll() with status {}", HttpStatus.OK);
        return ResponseEntity.ok(issueEventService.getByProjectId(projectId));
    }

    /**
     * Delete a single event.
     *
     * @param projectId
     *         The ID of the project.
     * @param eventId
     *         The ID of the event.
     * @return 204 - No Content on success.
     * @throws ProjectForbiddenException
     */
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = RESOURCE_URL + "/{eventId}",
            produces = MediaType.APPLICATION_JSON
    )
    private ResponseEntity delete(@PathVariable("projectId") Long projectId,
                                  @PathVariable("eventId") int eventId)
            throws ProjectForbiddenException {
        log.info("Entering delete(projectId: {}, eventId: {})", projectId, eventId);
        jiraUtils.checkIfProjectIsAllowed(projectId);
        final int numDeleted = issueEventService.deleteById(eventId);
        if (numDeleted == 0) {
            log.error("Leaving delete() with status {}", HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new RestError(HttpStatus.BAD_REQUEST, "The event could not be deleted."));
        } else {
            log.info("Leaving delete() with status {}", HttpStatus.OK);
            return ResponseEntity.noContent().build();
        }
    }
}
