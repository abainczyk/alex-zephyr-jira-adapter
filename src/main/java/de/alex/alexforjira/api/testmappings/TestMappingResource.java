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

package de.alex.alexforjira.api.testmappings;

import de.alex.alexforjira.api.alex.AlexEndpoints;
import de.alex.alexforjira.api.alex.entities.AlexTestCase;
import de.alex.alexforjira.api.jira.JiraEndpoints;
import de.alex.alexforjira.api.jira.entities.JiraIssue;
import de.alex.alexforjira.db.h2.tables.pojos.TestMapping;
import de.alex.alexforjira.security.ProjectForbiddenException;
import de.alex.alexforjira.shared.JiraUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
public class TestMappingResource {

    private static final String RESOURCE_URL = "/rest/projects/{jiraProjectId}/testMappings";

    private final TestMappingService testMappingService;

    private final AlexEndpoints alexEndpoints;

    private final JiraEndpoints jiraEndpoints;

    private final JiraUtils jiraUtils;

    @Autowired
    public TestMappingResource(final TestMappingService testMappingService,
                               final AlexEndpoints alexEndpoints,
                               final JiraEndpoints jiraEndpoints,
                               final JiraUtils jiraUtils) {
        this.testMappingService = testMappingService;
        this.alexEndpoints = alexEndpoints;
        this.jiraEndpoints = jiraEndpoints;
        this.jiraUtils = jiraUtils;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity getAll(final @PathVariable("jiraProjectId") Long jiraProjectId) throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(jiraProjectId);
        return ResponseEntity.ok(testMappingService.findByJiraProjectId(jiraProjectId));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL,
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity create(final @PathVariable("jiraProjectId") Long jiraProjectId,
                                 final @RequestBody TestMapping testMapping) throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(jiraProjectId);
        jiraUtils.checkIfProjectIsAllowed(testMapping.getJiraProjectId());
        final Long jiraTestId = testMapping.getJiraTestId();

        final TestMapping existingTestMapping = testMappingService.findOneByJiraTestId(jiraTestId);
        if (existingTestMapping != null) {
            return ResponseEntity.ok(existingTestMapping);
        }

        // get the name of the test in jira so that a new test with the same name is created in ALEX
        final Response res1 = jiraEndpoints.issue(jiraTestId).get();

        if (res1.getStatus() != Response.Status.OK.getStatusCode()) {
            return ResponseEntity.badRequest().body("The test with the id '" + jiraTestId + "' could not be fetched.");
        }

        final JiraIssue test = res1.readEntity(JiraIssue.class);
        final String testName = test.getFields().getSummary();

        // create a new test in ALEX with the project
        if (testMapping.getAlexTestId() == null) {
            final AlexTestCase testCase = new AlexTestCase(testName, testMapping.getAlexProjectId(), null);

            final Response res2 = alexEndpoints.tests(testMapping.getAlexProjectId()).post(Entity.json(testCase));

            if (res2.getStatus() != Response.Status.CREATED.getStatusCode()) {
                return ResponseEntity.badRequest().body("The test could not be created in ALEX.");
            }

            final AlexTestCase createdTestCase = res2.readEntity(AlexTestCase.class);
            testMapping.setAlexTestId(createdTestCase.getId());
        }

        final TestMapping createdTestMapping = testMappingService.create(testMapping);
        return ResponseEntity.ok(createdTestMapping);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = RESOURCE_URL + "/{mappingId}"
    )
    public ResponseEntity delete(final @PathVariable("jiraProjectId") Long jiraProjectId,
                                 final @PathVariable("mappingId") int mappingId) throws ProjectForbiddenException {
        jiraUtils.checkIfProjectIsAllowed(jiraProjectId);
        testMappingService.deleteById(mappingId);
        return ResponseEntity.noContent().build();
    }
}
