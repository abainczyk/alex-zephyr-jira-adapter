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

package de.alex.jirazapidemo.api.testmappings;

import de.alex.jirazapidemo.alex.AlexEndpoints;
import de.alex.jirazapidemo.alex.AlexResource;
import de.alex.jirazapidemo.alex.entities.AlexTestCase;
import de.alex.jirazapidemo.db.h2.tables.pojos.TestMapping;
import de.alex.jirazapidemo.jira.JiraEndpoints;
import de.alex.jirazapidemo.jira.JiraResource;
import de.alex.jirazapidemo.jira.entities.JiraIssue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
public class TestMappingResource {

    private final String RESOURCE_URL = "/rest/projects/{jiraProjectId}/tests/{jiraTestId}/mappings";

    @Autowired
    private TestMappingService testMappingService;

    @Autowired
    private AlexEndpoints alexEndpoints;

    @Autowired
    private JiraEndpoints jiraEndpoints;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/rest/projects/{jiraProjectId}/testMappings"
    )
    public ResponseEntity getAll(@PathVariable("jiraProjectId") Long jiraProjectId) {
        return ResponseEntity.ok(testMappingService.findByJiraProjectId(jiraProjectId));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    public ResponseEntity create(@PathVariable("jiraProjectId") Long jiraProjectId,
                                 @PathVariable("jiraTestId") Long jiraTestId,
                                 @RequestBody TestMapping testMapping) {

        final TestMapping existingTestMapping = testMappingService.findOneByJiraProjectIdAndJiraTestId(jiraProjectId, jiraTestId);
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
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity get(@PathVariable("jiraProjectId") Long jiraProjectId,
                              @PathVariable("jiraTestId") Long jiraTestId) {

        final TestMapping mapping = testMappingService.findOneByJiraProjectIdAndJiraTestId(jiraProjectId, jiraTestId);
        return ResponseEntity.ok(mapping);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = RESOURCE_URL
    )
    public ResponseEntity delete(@PathVariable("jiraProjectId") Long jiraProjectId,
                                 @PathVariable("jiraTestId") Long jiraTestId) {
        testMappingService.deleteAllByJiraTestId(jiraTestId);
        return ResponseEntity.noContent().build();
    }
}
