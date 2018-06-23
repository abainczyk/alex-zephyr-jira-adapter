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

package de.alex.alexforjira.api.executions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** Resource for handling the test execution. */
@RestController
public class ExecutionResource {

    private static final Logger log = LoggerFactory.getLogger(ExecutionResource.class);

    private static final String RESOURCE_URL = "/rest/executions";

    private final ExecutionService executionService;

    @Autowired
    public ExecutionResource(ExecutionService executionService) {
        this.executionService = executionService;
    }

    /**
     * Get the status of the current execution.
     *
     * @return HTTP status 200 and the status.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/status",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity getStatus() {
        log.info("Entering getStatus()");
        final ExecutionStatus status = executionService.getStatus();
        log.info("Leaving getStatus() with {}", status);
        return ResponseEntity.ok(status);
    }

    /**
     * Abort the test execution.
     *
     * @return HTTP status 200.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/abort",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity abort() {
        log.info("Entering abort()");
        executionService.abort();
        log.info("Leaving abort()");
        return ResponseEntity.ok().build();
    }
}
