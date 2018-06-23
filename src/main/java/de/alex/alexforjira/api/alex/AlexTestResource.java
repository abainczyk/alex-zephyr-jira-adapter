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

package de.alex.alexforjira.api.alex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** Resource that works as a proxy to the REST API of ALEX for tests. */
@RestController
public class AlexTestResource {

    private static final Logger log = LoggerFactory.getLogger(AlexTestResource.class);

    private final String RESOURCE_URL = "/rest/alex/projects/{projectId}/tests";

    private final AlexEndpoints alexEndpoints;

    @Autowired
    public AlexTestResource(final AlexEndpoints alexEndpoints) {
        this.alexEndpoints = alexEndpoints;
    }

    /**
     * Get the root test suite that contains all other test suites and test cases.
     *
     * @param projectId
     *         The ID of the ALEX project.
     * @return The root test suite.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/root",
            produces = MediaType.APPLICATION_JSON
    )
    private ResponseEntity getRoot(final @PathVariable("projectId") Long projectId) {
        log.info("Entering getRoot(projectId: {})", projectId);
        final Response res = alexEndpoints.rootTest(projectId).get();

        log.info("Leaving getRoot() with status {}", res.getStatus());
        return ResponseEntity.status(res.getStatus()).body(res.readEntity(String.class));
    }
}
