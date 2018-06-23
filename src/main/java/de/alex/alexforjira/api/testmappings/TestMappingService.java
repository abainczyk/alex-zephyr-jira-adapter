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

    @Transactional
    public TestMapping findOneByJiraTestId(final Long jiraTestId) {
        final Record record = dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .fetchOne();

        return record == null ? null : record.into(new TestMapping());
    }

    @Transactional
    public List<TestMapping> findByAlexProjectId(final Long alexProjectId) {
        return dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_PROJECT_ID.eq(alexProjectId))
                .fetch()
                .map(record -> record.into(new TestMapping()));
    }

    @Transactional
    public List<TestMapping> findByJiraProjectId(final Long jiraProjectId) {
        return dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .fetch()
                .map(record -> record.into(new TestMapping()));
    }

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

    @Transactional
    public void deleteAllByJiraProjectId(final Long jiraProjectId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .execute();
    }

    @Transactional
    public void deleteAllByAlexProjectId(final Long alexProjectId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_PROJECT_ID.eq(alexProjectId))
                .execute();
    }

    @Transactional
    public void deleteByAlexTestId(final Long alexTestId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_TEST_ID.eq(alexTestId))
                .execute();
    }

    @Transactional
    public int deleteByJiraTestId(final Long jiraTestId) {
        return dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .execute();
    }

    @Transactional
    public int deleteById(final int id) {
        return dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ID.eq(id))
                .execute();
    }

    @Transactional
    public boolean existsByTestId(Long jiraTestId) {
        return dsl.select().from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .fetch()
                .size() > 0;
    }

    @Transactional
    public void incrementTestUpdates(final Long alexTestId) {
        dsl.update(TEST_MAPPING)
                .set(TEST_MAPPING.UPDATES, TEST_MAPPING.UPDATES.add(1))
                .where(TEST_MAPPING.ALEX_TEST_ID.eq(alexTestId))
                .execute();
    }

    @Transactional
    public void resetTestUpdates(final Long alexTestId) {
        dsl.update(TEST_MAPPING)
                .set(TEST_MAPPING.UPDATES, 0)
                .where(TEST_MAPPING.ALEX_TEST_ID.eq(alexTestId))
                .execute();
    }
}
