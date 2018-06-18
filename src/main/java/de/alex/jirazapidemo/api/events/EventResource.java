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

package de.alex.jirazapidemo.api.events;

import de.alex.jirazapidemo.db.h2.tables.pojos.IssueEvent;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventResource {

    private final String RESOURCE_URL = "/rest/events/projects";

    @Autowired
    private IssueEventService issueEventService;

    @Autowired
    private ProjectEventService projectEventService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    private ResponseEntity<List<ProjectEvent>> getProjectEvents() {
        return ResponseEntity.ok(projectEventService.get());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}/issues"
    )
    private ResponseEntity<List<IssueEvent>> getIssueEvents(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueEventService.getByProjectId(projectId));
    }

}
