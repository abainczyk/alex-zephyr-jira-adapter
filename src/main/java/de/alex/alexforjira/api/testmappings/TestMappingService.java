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

import de.alex.alexforjira.db.h2.tables.pojos.TestMapping;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestMappingService {

    private static final de.alex.alexforjira.db.h2.tables.TestMapping TEST_MAPPING
            = de.alex.alexforjira.db.h2.tables.TestMapping.TEST_MAPPING;

    private final DSLContext dsl;

    @Autowired
    public TestMappingService(final DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Get a test mapping by a Jira test ID
     *
     * @param jiraTestId
     *         The ID of the test in Jira.
     * @return The test mapping or null if none found.
     */
    @Transactional
    public TestMapping findOneByJiraTestId(final Long jiraTestId) {
        final Record record = dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .fetchOne();

        return record == null ? null : record.into(new TestMapping());
    }

    /**
     * Get all test mappings by ALEX project ID.
     *
     * @param alexProjectId
     *         The ID of the ALEX project.
     * @return All related test mappings.
     */
    @Transactional
    public List<TestMapping> findByAlexProjectId(final Long alexProjectId) {
        return dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_PROJECT_ID.eq(alexProjectId))
                .fetch()
                .map(record -> record.into(new TestMapping()));
    }

    /**
     * Get all test mappings by Jira project ID.
     *
     * @param jiraProjectId
     *         The ID of the Jira project.
     * @return All related test mappings.
     */
    @Transactional
    public List<TestMapping> findByJiraProjectId(final Long jiraProjectId) {
        return dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .fetch()
                .map(record -> record.into(new TestMapping()));
    }

    /**
     * Create a new test mapping.
     *
     * @param mapping
     *         The test mapping to create.
     * @return The created test mapping.
     */
    @Transactional
    public TestMapping create(final TestMapping mapping) {
        return dsl.insertInto(TEST_MAPPING, TEST_MAPPING.ALEX_PROJECT_ID, TEST_MAPPING.ALEX_TEST_ID,
                              TEST_MAPPING.JIRA_PROJECT_ID, TEST_MAPPING.JIRA_TEST_ID)
                .values(mapping.getAlexProjectId(), mapping.getAlexTestId(), mapping.getJiraProjectId(),
                        mapping.getJiraTestId())
                .returning()
                .fetchOne()
                .into(new TestMapping());
    }

    /**
     * Delete all test mappings by a Jira project ID.
     *
     * @param jiraProjectId
     *         The ID of the project.
     */
    @Transactional
    public void deleteAllByJiraProjectId(final Long jiraProjectId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .execute();
    }

    /**
     * Delete all test mappings by a ALEX project ID.
     *
     * @param alexProjectId
     *         The ID of the project.
     */
    @Transactional
    public void deleteAllByAlexProjectId(final Long alexProjectId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_PROJECT_ID.eq(alexProjectId))
                .execute();
    }

    /**
     * Delete a single test mapping by a ALEX test ID.
     *
     * @param alexTestId
     *         The ID of the test.
     */
    @Transactional
    public void deleteByAlexTestId(final Long alexTestId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_TEST_ID.eq(alexTestId))
                .execute();
    }

    /**
     * Delete a single test mapping by a Jira test ID.
     *
     * @param jiraTestId
     *         The ID of the test.
     */
    @Transactional
    public void deleteByJiraTestId(final Long jiraTestId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .execute();
    }

    /**
     * Delete a test mapping by its ID.
     *
     * @param id
     *         The ID of the test mapping.
     */
    @Transactional
    public void deleteById(final int id) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ID.eq(id))
                .execute();
    }

    /**
     * Check if there exists a test mapping for a test in Jira.
     *
     * @param jiraTestId
     *         The ID of the test in Jira.
     * @return If a mapping for the test exists.
     */
    @Transactional
    public boolean existsByTestId(Long jiraTestId) {
        return dsl.select().from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .fetch()
                .size() > 0;
    }

    /**
     * Increment the counter for updates of a test mapping.
     *
     * @param alexTestId
     *         The ID of the test in ALEX.
     */
    @Transactional
    public void incrementTestUpdates(final Long alexTestId) {
        dsl.update(TEST_MAPPING)
                .set(TEST_MAPPING.UPDATES, TEST_MAPPING.UPDATES.add(1))
                .where(TEST_MAPPING.ALEX_TEST_ID.eq(alexTestId))
                .execute();
    }

    /**
     * Reset the counter for updates of a test mapping.
     *
     * @param alexTestId
     *         The ID of the test in ALEX.
     */
    @Transactional
    public void resetTestUpdates(final Long alexTestId) {
        dsl.update(TEST_MAPPING)
                .set(TEST_MAPPING.UPDATES, 0)
                .where(TEST_MAPPING.ALEX_TEST_ID.eq(alexTestId))
                .execute();
    }
}
