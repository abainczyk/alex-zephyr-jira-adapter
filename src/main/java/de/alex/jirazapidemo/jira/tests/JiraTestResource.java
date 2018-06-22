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

package de.alex.jirazapidemo.jira.tests;

import de.alex.jirazapidemo.alex.AlexEndpoints;
import de.alex.jirazapidemo.alex.entities.AlexTestCase;
import de.alex.jirazapidemo.alex.entities.AlexTestCaseStep;
import de.alex.jirazapidemo.api.testmappings.TestMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.TestMapping;
import de.alex.jirazapidemo.jira.JiraEndpoints;
import de.alex.jirazapidemo.jira.entities.JiraTestStep;
import de.alex.jirazapidemo.utils.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
public class JiraTestResource {

    private static final String RESOURCE_URL = "/rest/jira/projects/{projectId}/tests";

    private final TestMappingService testMappingService;

    private final AlexEndpoints alexEndpoints;

    private final JiraEndpoints jiraEndpoints;

    @Autowired
    public JiraTestResource(TestMappingService testMappingService,
                            AlexEndpoints alexEndpoints,
                            JiraEndpoints jiraEndpoints) {
        this.testMappingService = testMappingService;
        this.alexEndpoints = alexEndpoints;
        this.jiraEndpoints = jiraEndpoints;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity getTestsByProjectId(@PathVariable("projectId") Long projectId) {
        final Response response = jiraEndpoints.tests(projectId).get();
        return ResponseEntity.status(response.getStatus()).body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/{testId}/update"
    )
    public ResponseEntity update(@PathVariable("projectId") Long projectId, @PathVariable("testId") Long testId) {
        final TestMapping testMapping = testMappingService.findOneByJiraProjectIdAndJiraTestId(projectId, testId);
        if (testMapping == null) {
            return ResponseEntity.badRequest().body(new RestError(HttpStatus.BAD_REQUEST, "Test mapping not defined."));
        }

        final Response res1 = alexEndpoints.test(testMapping.getAlexProjectId(), testMapping.getAlexTestId()).get();
        final Response res2 = jiraEndpoints.testSteps(testId).get();

        try {
            final AlexTestCase alexTest = res1.readEntity(AlexTestCase.class);
            final LinkedList<JiraTestStep> jiraSteps = res2.readEntity(new GenericType<LinkedList<JiraTestStep>>() {
            });

            while (alexTest.getSteps().size() < jiraSteps.size()) {
                final JiraTestStep lastStep = jiraSteps.removeLast();

                final Response res = jiraEndpoints.testStep(testId, lastStep.getId()).delete();
                if (res.getStatus() != HttpStatus.OK.value()) {
                    throw new Exception("Could not remove step " + lastStep.getOrderId() + ".");
                }
            }

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

            for (int i = 0; i < alexTest.getSteps().size(); i++) {
                final AlexTestCaseStep alexStep = alexTest.getSteps().get(i);
                final JiraTestStep jiraStep = jiraSteps.get(i);

                String description = alexStep.getSymbol().getDescription();
                description = description == null ? "" : "\n" + description;

                jiraStep.setOrderId((long) i);
                jiraStep.setStep(alexStep.getSymbol().getName() + description);
                jiraStep.setResult(alexStep.getExpectedResult());

                List<String> data = new ArrayList<>();
                alexStep.getVisibleParameterValues().forEach(val -> {
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
            return ResponseEntity.badRequest().body(new RestError(HttpStatus.BAD_REQUEST, e.getMessage()));
        }

        return ResponseEntity.ok().build();
    }
}
