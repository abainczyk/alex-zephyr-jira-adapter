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

package de.alex.jirazapidemo.api.projectmappings;

import de.alex.jirazapidemo.api.events.IssueEventService;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectMappingResource {

    private static final Logger log = LoggerFactory.getLogger(ProjectMappingResource.class);

    private static final String RESOURCE_URL = "/rest/projectMappings";

    private final ProjectMappingService projectMappingService;

    private final IssueEventService issueEventService;

    @Autowired
    public ProjectMappingResource(ProjectMappingService projectMappingService,
                                  IssueEventService issueEventService) {
        this.projectMappingService = projectMappingService;
        this.issueEventService = issueEventService;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity<List<ProjectMapping>> getAll() {
        final List<ProjectMapping> mappings = projectMappingService.getAll();
        return ResponseEntity.ok(mappings);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    public ResponseEntity<ProjectMapping> create(@RequestBody final ProjectMapping projectMapping) {
        final ProjectMapping mapping = projectMappingService.create(projectMapping);
        return ResponseEntity.ok(mapping);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/byJiraProjectId/{projectId}"
    )
    public ResponseEntity<ProjectMapping> getByJiraProjectId(@PathVariable("projectId") final Long projectId) {
        final ProjectMapping mapping = projectMappingService.getByJiraProjectId(projectId);
        return mapping == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(mapping);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = RESOURCE_URL + "/byJiraProjectId/{projectId}"
    )
    public ResponseEntity<ProjectMapping> delete(@PathVariable("projectId") final Long projectId) {
        log.info("Entering delete(projectId: {})", projectId);
        projectMappingService.deleteByJiraProjectId(projectId);
        issueEventService.deleteByProjectId(projectId);
        return ResponseEntity.noContent().build();
    }

}
