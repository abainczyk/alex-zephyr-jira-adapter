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

package de.alex.alexforjira.api.projectmappings;

import de.alex.alexforjira.db.h2.tables.pojos.ProjectMapping;
import de.alex.alexforjira.security.ProjectForbiddenException;
import de.alex.alexforjira.shared.JiraUtils;
import de.alex.alexforjira.shared.RestError;
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

import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
public class ProjectMappingResource {

    private static final Logger log = LoggerFactory.getLogger(ProjectMappingResource.class);

    private static final String RESOURCE_URL = "/rest/projectMappings";

    private final ProjectMappingService projectMappingService;

    private final JiraUtils jiraUtils;

    @Autowired
    public ProjectMappingResource(final ProjectMappingService projectMappingService, final JiraUtils jiraUtils) {
        this.projectMappingService = projectMappingService;
        this.jiraUtils = jiraUtils;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity<List<ProjectMapping>> getAll() {
        log.info("Entering getAll()");
        final List<ProjectMapping> mappings = projectMappingService.getAll();
        log.info("Leaving delete() with status {}", HttpStatus.OK);
        return ResponseEntity.ok(mappings);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL,
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON
    )
    public ResponseEntity create(@RequestBody final ProjectMapping projectMapping) throws ProjectForbiddenException {
        log.info("Entering delete({})", projectMapping);
        jiraUtils.checkIfProjectIsAllowed(projectMapping.getJiraProjectId());

        try {
            final ProjectMapping mapping = projectMappingService.create(projectMapping);
            log.info("Leaving delete() with status {}", HttpStatus.OK);
            return ResponseEntity.ok(mapping);
        } catch (Exception e) {
            log.info("Leaving delete() with status {}", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RestError(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/byJiraProjectId/{projectId}",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity<ProjectMapping> getByJiraProjectId(@PathVariable("projectId") final Long projectId)
            throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(projectId);
        final ProjectMapping mapping = projectMappingService.getByJiraProjectId(projectId);
        return mapping == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(mapping);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = RESOURCE_URL + "/{mappingId}"
    )
    public ResponseEntity delete(@PathVariable("mappingId") final int mappingId) {
        log.info("Entering delete(mappingId: {})", mappingId);

        final ProjectMapping mapping = projectMappingService.getById(mappingId);
        if (mapping == null) {
            log.error("Leaving delete() with status {}", HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestError(HttpStatus.NOT_FOUND, "The mapping could not be found."));
        } else {
            projectMappingService.deleteById(mappingId);
            log.info("Leaving delete() with status {}", HttpStatus.NO_CONTENT);
            return ResponseEntity.noContent().build();
        }
    }
}
