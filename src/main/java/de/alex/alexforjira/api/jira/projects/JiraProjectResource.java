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

package de.alex.alexforjira.api.jira.projects;

import de.alex.alexforjira.api.jira.JiraEndpoints;
import de.alex.alexforjira.api.jira.entities.JiraProject;
import de.alex.alexforjira.security.ProjectForbiddenException;
import de.alex.alexforjira.shared.JiraUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/** Works as a proxy to the Jira REST API for projects. The returning status code depends on the answer of the API. */
@RestController
public class JiraProjectResource {

    private static final Logger log = LoggerFactory.getLogger(JiraProjectResource.class);

    private static final String RESOURCE_URL = "/rest/jira/projects";

    private final JiraEndpoints jiraEndpoints;

    private final JiraUtils jiraUtils;

    @Autowired
    public JiraProjectResource(final JiraEndpoints jiraEndpoints, final JiraUtils jiraUtils) {
        this.jiraEndpoints = jiraEndpoints;
        this.jiraUtils = jiraUtils;
    }

    /**
     * Get all projects in Jira.
     *
     * @return The projects.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity getAll() {
        log.info("Entering getAll()");
        final Response res = jiraEndpoints.projects().get();
        final List<JiraProject> projects = res.readEntity(new GenericType<List<JiraProject>>() {
        });

        log.info("Leaving getAll() with status {}", res.getStatus());
        return ResponseEntity.status(res.getStatus()).body(jiraUtils.filterAllowedProjects(projects));
    }

    /**
     * Get a single project by its ID.
     *
     * @param projectId
     *         The ID of the project.
     * @return The project.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity get(@PathVariable("projectId") Long projectId) throws ProjectForbiddenException {
        log.info("Entering get(projectId: {})", projectId);
        jiraUtils.checkIfProjectIsAllowed(projectId);
        final Response res = jiraEndpoints.project(projectId).get();

        log.info("Leaving get(projectId: {}) with status {}", projectId, res.getStatus());
        return ResponseEntity.status(res.getStatus()).body(res.readEntity(String.class));
    }
}
