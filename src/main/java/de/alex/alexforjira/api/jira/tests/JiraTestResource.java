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

package de.alex.alexforjira.api.jira.tests;

import de.alex.alexforjira.api.alex.AlexEndpoints;
import de.alex.alexforjira.api.alex.AlexProjectsResource;
import de.alex.alexforjira.api.alex.entities.AlexTestCase;
import de.alex.alexforjira.api.alex.entities.AlexTestCaseStep;
import de.alex.alexforjira.api.executions.ExecutionService;
import de.alex.alexforjira.api.executions.entities.ExecutionConfig;
import de.alex.alexforjira.api.jira.JiraEndpoints;
import de.alex.alexforjira.api.jira.entities.JiraTestStep;
import de.alex.alexforjira.api.testmappings.TestMappingService;
import de.alex.alexforjira.db.h2.tables.pojos.TestMapping;
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

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class JiraTestResource {

    private static final Logger log = LoggerFactory.getLogger(AlexProjectsResource.class);

    private static final String RESOURCE_URL = "/rest/jira/projects/{projectId}/tests";

    private final TestMappingService testMappingService;

    private final ExecutionService executionService;

    private final AlexEndpoints alexEndpoints;

    private final JiraEndpoints jiraEndpoints;

    private final JiraUtils jiraUtils;

    @Autowired
    public JiraTestResource(TestMappingService testMappingService,
                            ExecutionService executionService,
                            AlexEndpoints alexEndpoints,
                            JiraEndpoints jiraEndpoints,
                            JiraUtils jiraUtils) {
        this.testMappingService = testMappingService;
        this.executionService = executionService;
        this.alexEndpoints = alexEndpoints;
        this.jiraEndpoints = jiraEndpoints;
        this.jiraUtils = jiraUtils;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity getTestsByProjectId(@PathVariable("projectId") Long projectId)
            throws ProjectForbiddenException {
        log.info("Entering getTestsByProjectId(projectId: {})", projectId);
        jiraUtils.checkIfProjectIsAllowed(projectId);

        final Response response = jiraEndpoints.tests(projectId).get();
        log.info("Leaving getTestsByProjectId() with status {}", response.getStatus());
        return ResponseEntity.status(response.getStatus()).body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/{testId}/execute"
    )
    public ResponseEntity execute(@PathVariable("projectId") Long projectId,
                                  @PathVariable("testId") Long testId,
                                  @RequestBody ExecutionConfig config)
            throws ProjectForbiddenException {
        log.info("Entering execute(projectId: {}, testId: {}, config: {})", projectId, testId, config);
        jiraUtils.checkIfProjectIsAllowed(projectId);

        try {
            executionService.executeTest(config);
            log.info("Leaving execute() with status {}", HttpStatus.OK);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            final RestError error = new RestError(HttpStatus.BAD_REQUEST, e.getMessage());
            log.error("Leaving execute() with error ", error);
            return ResponseEntity.badRequest().body(error);
        }
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/{testId}/update"
    )
    public ResponseEntity update(@PathVariable("projectId") Long projectId, @PathVariable("testId") Long testId)
            throws ProjectForbiddenException {
        log.info("Entering update(projectId: {}, testId: {})", projectId, testId);
        jiraUtils.checkIfProjectIsAllowed(projectId);

        final TestMapping testMapping = testMappingService.findOneByJiraTestId(testId);
        if (testMapping == null) {
            return ResponseEntity.badRequest().body(new RestError(HttpStatus.BAD_REQUEST, "Test mapping not defined."));
        }

        final Response res1 = alexEndpoints.test(testMapping.getAlexProjectId(), testMapping.getAlexTestId()).get();
        final Response res2 = jiraEndpoints.testSteps(testId).get();

        try {
            final AlexTestCase alexTest = res1.readEntity(AlexTestCase.class);
            final LinkedList<JiraTestStep> jiraSteps = res2.readEntity(new GenericType<LinkedList<JiraTestStep>>() {
            });

            // if there are more test steps in Jira than in ALEX, we remove the last |#stepsJira - #stepsAlex| test
            // steps in Jira
            while (alexTest.getSteps().size() < jiraSteps.size()) {
                final JiraTestStep lastStep = jiraSteps.removeLast();

                final Response res = jiraEndpoints.testStep(testId, lastStep.getId()).delete();
                if (res.getStatus() != HttpStatus.OK.value()) {
                    throw new Exception("Could not remove step " + lastStep.getOrderId() + ".");
                }
            }

            // if there are more test steps in ALEX than in Jira, we create |#stepsAlex - #stepsJira| additional
            // dummy test steps in Jira
            long orderId = alexTest.getSteps().size() + 1;
            while (alexTest.getSteps().size() > jiraSteps.size()) {
                final JiraTestStep jiraStep = new JiraTestStep();

                jiraStep.setOrderId(orderId);
                jiraStep.setStep("dummy step");
                jiraStep.setData("dummy data");
                jiraStep.setResult("dummy result");
                orderId++;

                final Response res = jiraEndpoints.testSteps(testId).post(Entity.json(jiraStep));
                if (res.getStatus() != HttpStatus.OK.value()) {
                    throw new Exception("Could not create a new step.");
                }

                jiraSteps.add(res.readEntity(JiraTestStep.class));
            }

            // update the test steps
            for (int i = 0; i < alexTest.getSteps().size(); i++) {
                final AlexTestCaseStep alexStep = alexTest.getSteps().get(i);
                final JiraTestStep jiraStep = jiraSteps.get(i);

                String description = alexStep.getpSymbol().getSymbol().getDescription();
                description = description == null ? "" : "\n" + description;

                jiraStep.setOrderId((long) i);
                jiraStep.setStep(alexStep.getpSymbol().getSymbol().getName() + description);
                jiraStep.setResult(alexStep.getExpectedResult());

                final List<String> data = new ArrayList<>();
                alexStep.getpSymbol().getVisibleParameterValues().forEach(val -> {
                    data.add(val.getParameter().getName() + ": " + val.getValue());
                });

                jiraStep.setData(String.join("\n", data));

                final Response res = jiraEndpoints.testStep(testId, jiraStep.getId()).put(Entity.json(jiraStep));
                if (res.getStatus() != HttpStatus.OK.value()) {
                    throw new Exception("Could not update step " + jiraStep.getOrderId() + ".");
                }
            }

            testMappingService.resetTestUpdates(alexTest.getId());
        } catch (Exception e) {
            final RestError error = new RestError(HttpStatus.BAD_REQUEST, e.getMessage());
            log.error("Leaving update() with error {}", error);
            return ResponseEntity.badRequest().body(error);
        }

        log.info("Leaving update() with status {}", HttpStatus.OK);
        return ResponseEntity.ok().build();
    }
}
