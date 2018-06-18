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

package de.alex.jirazapidemo.api.sync;

import de.alex.jirazapidemo.alex.AlexEndpoints;
import de.alex.jirazapidemo.alex.entities.AlexProject;
import de.alex.jirazapidemo.alex.entities.AlexTestCase;
import de.alex.jirazapidemo.api.projectmappings.ProjectMappingService;
import de.alex.jirazapidemo.api.testmappings.TestMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.TestMapping;
import de.alex.jirazapidemo.jira.JiraEndpoints;
import de.alex.jirazapidemo.jira.entities.JiraIssue;
import de.alex.jirazapidemo.jira.entities.JiraJqlIssueResult;
import de.alex.jirazapidemo.jira.projects.JiraProject;
import de.alex.jirazapidemo.services.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private ProjectMappingService projectMappingService;

    private TestMappingService testMappingService;

    private AlexEndpoints alexEndpoints;

    private JiraEndpoints jiraEndpoints;

    @Autowired
    public SyncService(ProjectMappingService projectMappingService,
                       TestMappingService testMappingService,
                       AlexEndpoints alexEndpoints,
                       JiraEndpoints jiraEndpoints) {
        this.projectMappingService = projectMappingService;
        this.testMappingService = testMappingService;
        this.alexEndpoints = alexEndpoints;
        this.jiraEndpoints = jiraEndpoints;
    }

    public void sync() {
        syncProjects();
        syncTests();
    }

    private void syncProjects() {
        log.info("synchronizing projects...");
        log.info("check if all projects in ALEX still exist");

        final List<Long> alexProjectIds = projectMappingService.getAllAlexProjectIds();
        if (alexProjectIds.size() > 0) {
            final Response res = alexEndpoints.projects().get();

            if (res.getStatus() == Response.Status.OK.getStatusCode()) {
                final List<Long> ids = res.readEntity(new GenericType<List<AlexProject>>() {
                }).stream()
                        .map(AlexProject::getId)
                        .collect(Collectors.toList());

                // if the project id does not exists, delete everything related to the project.
                alexProjectIds.forEach(id -> {
                    if (!ids.contains(id)) {
                        log.info("delete by alex project: " + id);
                        projectMappingService.deleteByAlexProjectId(id);
                    }
                });
            }
        }

        log.info("check if all projects in Jira still exist.");
        final List<Long> jiraProjectIds = projectMappingService.getAllJiraProjectIds();
        if (jiraProjectIds.size() > 0) {
            final Response res = jiraEndpoints.projects().get();

            if (res.getStatus() == Response.Status.OK.getStatusCode()) {
                final List<String> ids = res.readEntity(new GenericType<List<JiraProject>>() {
                }).stream()
                        .map(JiraProject::getId)
                        .collect(Collectors.toList());

                // if the project id does not exists, delete everything related to the project.
                jiraProjectIds.forEach(id -> {
                    if (!ids.contains(String.valueOf(id))) {
                        log.info("delete by jira project: " + id);
                        projectMappingService.deleteByJiraProjectId(id);
                    }
                });
            }
        }

        log.info("synchronization of projects completed\n");
    }

    private void syncTests() {
        log.info("synchronizing tests...");
        log.info("check if all tests in ALEX still exist");

        final List<Long> alexProjectIds = projectMappingService.getAllAlexProjectIds();
        for (Long projectId : alexProjectIds) {
            final List<Long> alexTestIds = testMappingService.findByAlexProjectId(projectId).stream()
                    .map(TestMapping::getAlexTestId)
                    .collect(Collectors.toList());

            if (!alexTestIds.isEmpty()) {
                final Response res = alexEndpoints.testCases(projectId).get();

                final List<Long> ids = res.readEntity(new GenericType<List<AlexTestCase>>() {
                }).stream()
                        .map(AlexTestCase::getId)
                        .collect(Collectors.toList());

                alexTestIds.forEach(id -> {
                    if (!ids.contains(id)) {
                        log.info("delete by alex test: " + id);
                        testMappingService.deleteAllByAlexTestId(id);
                    }
                });
            }
        }

        log.info("check if all tests in Jira still exist");
        final List<Long> jiraProjectIds = projectMappingService.getAllJiraProjectIds();
        for (Long projectId : jiraProjectIds) {
            final List<Long> jiraTestIds = testMappingService.findByJiraProjectId(projectId).stream()
                    .map(TestMapping::getJiraTestId)
                    .collect(Collectors.toList());

            if (!jiraTestIds.isEmpty()) {
                final Response res = jiraEndpoints.tests(String.valueOf(projectId)).get();

                final JiraJqlIssueResult result = res.readEntity(JiraJqlIssueResult.class);
                final List<Long> ids = result.getIssues().stream()
                        .map(JiraIssue::getId)
                        .collect(Collectors.toList());

                jiraTestIds.forEach(id -> {
                    if (!ids.contains(id)) {
                        log.info("delete by jira test: " + id);
                        testMappingService.deleteAllByJiraTestId(id);
                    }
                });
            }
        }

        log.info("synchronization of tests completed\n");
    }

}
