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

package de.alex.jirazapidemo.jira.projects;

import de.alex.jirazapidemo.jira.JiraEndpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
public class JiraProjectResource {

    private static final Logger log = LoggerFactory.getLogger(JiraProjectResource.class);

    private static final String RESOURCE_URL = "/rest/jira/projects";

    private final JiraEndpoints jiraEndpoints;

    @Autowired
    public JiraProjectResource(JiraEndpoints jiraEndpoints) {
        this.jiraEndpoints = jiraEndpoints;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity getAll() {
        log.info("Entering getAll()");
        final Response res = jiraEndpoints.projects().get();

        log.info("Leaving getAll() with status {}", res.getStatus());
        return ResponseEntity.status(res.getStatus()).body(res.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}"
    )
    public ResponseEntity get(@PathVariable("projectId") String projectId) {
        log.info("Entering get(projectId: {})", projectId);
        final Response res = jiraEndpoints.project(projectId).get();

        log.info("Leaving get(projectId: {}) with status {}", projectId, res.getStatus());
        return ResponseEntity.status(res.getStatus()).body(res.readEntity(String.class));
    }
}
