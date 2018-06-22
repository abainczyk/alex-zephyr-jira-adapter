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

package de.alex.jirazapidemo.alex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

/**
 * Resource that works as a proxy to the REST API of ALEX for tests.
 */
@RestController
public class AlexProjectsResource {

    private static final Logger log = LoggerFactory.getLogger(AlexProjectsResource.class);

    private final String RESOURCE_URL = "/rest/alex/projects";

    private final AlexEndpoints alexEndpoints;

    @Autowired
    public AlexProjectsResource(AlexEndpoints alexEndpoints) {
        this.alexEndpoints = alexEndpoints;
    }

    /**
     * Get all projects in ALEX.
     *
     * @return All projects in ALEX.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    private ResponseEntity getAll() {
        log.info("Entering getAll()");
        final Response res = alexEndpoints.projects().get();
        log.info("Leaving getAll() with status {}", res.getStatus());
        return ResponseEntity.status(res.getStatus()).body(res.readEntity(String.class));
    }

    /**
     * Get a single project in ALEX.
     *
     * @param projectId
     *         The ID of the project.
     * @return The project with the ID:
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}"
    )
    private ResponseEntity get(@PathVariable Long projectId) {
        log.info("Entering get(projectId: {})", projectId);
        final Response res = alexEndpoints.project(projectId).get();
        log.info("Leaving get() with status {}", res.getStatus());
        return ResponseEntity.status(res.getStatus()).body(res.readEntity(String.class));
    }
}
