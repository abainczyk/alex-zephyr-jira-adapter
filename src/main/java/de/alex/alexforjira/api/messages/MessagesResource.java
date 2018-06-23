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

import de.alex.alexforjira.db.h2.tables.pojos.IssueEvent;
import de.alex.alexforjira.utils.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
public class MessagesResource {

    private static final String RESOURCE_URL = "/rest/messages/projects";

    private final MessagesService issueEventService;

    @Autowired
    public MessagesResource(MessagesService issueEventService) {
        this.issueEventService = issueEventService;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}/issues",
            produces = MediaType.APPLICATION_JSON
    )
    private ResponseEntity<List<IssueEvent>> getIssueEvents(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueEventService.getByProjectId(projectId));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = RESOURCE_URL + "/{projectId}/issues/{eventId}",
            produces = MediaType.APPLICATION_JSON
    )
    private ResponseEntity deleteIssueEvent(@PathVariable("projectId") Long projectId,
                                            @PathVariable("eventId") int eventId) {
        final int numDeleted = issueEventService.deleteById(eventId);
        if (numDeleted == 0) {
            return ResponseEntity.badRequest().body(new RestError(HttpStatus.BAD_REQUEST, "The event could not be deleted."));
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
