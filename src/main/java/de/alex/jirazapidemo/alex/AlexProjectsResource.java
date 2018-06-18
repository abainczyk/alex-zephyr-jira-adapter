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

import de.alex.jirazapidemo.api.projectmappings.ProjectMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

@RestController
public class AlexProjectsResource extends AlexResource {

    private final String RESOURCE_URL = "/rest/alex/projects";

    @Autowired
    private ProjectMappingService projectMappingService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    private ResponseEntity create(@RequestBody String project) {
        final Response response = alexEndpoints.projects().post(Entity.json(project));

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    private ResponseEntity getAll() {
        final Response response = alexEndpoints.projects().get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}"
    )
    private ResponseEntity get(@PathVariable Long projectId) {
        final Response response = alexEndpoints.project(projectId).get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/byJiraProject/{projectId}"
    )
    private ResponseEntity getByJiraProjectId(@PathVariable Long projectId) {
        final ProjectMapping mapping = projectMappingService.getByJiraProjectId(projectId);
        if (mapping == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return get(mapping.getAlexProjectId());
    }

}
