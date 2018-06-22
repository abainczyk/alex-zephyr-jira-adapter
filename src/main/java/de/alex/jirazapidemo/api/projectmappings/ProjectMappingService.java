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

package de.alex.jirazapidemo.api.projectmappings;

import de.alex.jirazapidemo.api.testmappings.TestMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectMapping;
import de.alex.jirazapidemo.db.h2.tables.records.ProjectMappingRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectMappingService {

    private static final de.alex.jirazapidemo.db.h2.tables.ProjectMapping PROJECT_MAPPING
            = de.alex.jirazapidemo.db.h2.tables.ProjectMapping.PROJECT_MAPPING;

    private final DSLContext dsl;

    private final TestMappingService testMappingService;

    @Autowired
    public ProjectMappingService(DSLContext dsl,
                                 TestMappingService testMappingService) {
        this.dsl = dsl;
        this.testMappingService = testMappingService;
    }

    @Transactional
    public ProjectMapping create(final ProjectMapping mapping) {
        return dsl.insertInto(PROJECT_MAPPING, PROJECT_MAPPING.ALEX_PROJECT_ID, PROJECT_MAPPING.JIRA_PROJECT_ID)
                .values(mapping.getAlexProjectId(), mapping.getJiraProjectId())
                .returning()
                .fetchOne()
                .into(new ProjectMapping());
    }

    @Transactional
    public List<ProjectMapping> getAll() {
        return dsl.select()
                .from(PROJECT_MAPPING)
                .fetch()
                .stream()
                .map(r -> r.into(new ProjectMapping()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectMapping getByJiraProjectId(final Long jiraProjectId) {
        final ProjectMappingRecord record = (ProjectMappingRecord) dsl.select()
                .from(PROJECT_MAPPING)
                .where(PROJECT_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .fetchOne();

        return record == null ? null : record.into(new ProjectMapping());
    }

    /**
     * Get all ids of the projects in ALEX that are used.
     *
     * @return The ids.
     */
    @Transactional
    public List<Long> getAllAlexProjectIds() {
        return dsl.selectDistinct(PROJECT_MAPPING.ALEX_PROJECT_ID)
                .from(PROJECT_MAPPING)
                .fetch()
                .stream()
                .map(Record1::component1)
                .collect(Collectors.toList());
    }

    /**
     * Get all the ids of the projects in Jira that are used.
     *
     * @return The ids.
     */
    @Transactional
    public List<Long> getAllJiraProjectIds() {
        return dsl.selectDistinct(PROJECT_MAPPING.JIRA_PROJECT_ID)
                .from(PROJECT_MAPPING)
                .fetch()
                .stream()
                .map(Record1::component1)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByJiraProjectId(final Long jiraProjectId) {
        dsl.delete(PROJECT_MAPPING)
                .where(PROJECT_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .execute();

        testMappingService.deleteAllByJiraProjectId(jiraProjectId);
    }

    @Transactional
    public void deleteByAlexProjectId(final Long alexProjectId) {
        dsl.delete(PROJECT_MAPPING)
                .where(PROJECT_MAPPING.ALEX_PROJECT_ID.eq(alexProjectId))
                .execute();

        testMappingService.deleteAllByAlexProjectId(alexProjectId);
    }

}
