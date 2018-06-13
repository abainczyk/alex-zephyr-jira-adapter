package de.alex.jirazapidemo.api.testmappings;

import de.alex.jirazapidemo.db.h2.tables.pojos.TestMapping;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestMappingService {

    @Autowired
    private DSLContext dsl;

    private static final de.alex.jirazapidemo.db.h2.tables.TestMapping TEST_MAPPING
            = de.alex.jirazapidemo.db.h2.tables.TestMapping.TEST_MAPPING;

    @Transactional
    public TestMapping findOneByJiraProjectIdAndJiraTestId(Long jiraProjectId, Long jiraTestId) {
        final Record record = dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId)
                               .and(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId)))
                .fetchOne();

        return record == null ? null : record.into(new TestMapping());
    }

    @Transactional
    public List<TestMapping> findByAlexProjectId(Long alexProjectId) {
        return dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_PROJECT_ID.eq(alexProjectId))
                .fetch()
                .map(record -> record.into(new TestMapping()));
    }

    @Transactional
    public List<TestMapping> findByJiraProjectId(Long jiraProjectId) {
        return dsl.select()
                .from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .fetch()
                .map(record -> record.into(new TestMapping()));
    }

    @Transactional
    public TestMapping create(TestMapping mapping) {
        return dsl.insertInto(TEST_MAPPING, TEST_MAPPING.ALEX_PROJECT_ID, TEST_MAPPING.ALEX_TEST_ID,
                              TEST_MAPPING.JIRA_PROJECT_ID, TEST_MAPPING.JIRA_TEST_ID)
                .values(mapping.getAlexProjectId(), mapping.getAlexTestId(), mapping.getJiraProjectId(),
                        mapping.getJiraTestId())
                .returning()
                .fetchOne()
                .into(new TestMapping());
    }

    @Transactional
    public void deleteAllByJiraProjectId(Long jiraProjectId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_PROJECT_ID.eq(jiraProjectId))
                .execute();
    }

    @Transactional
    public void deleteAllByAlexProjectId(Long alexProjectId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_PROJECT_ID.eq(alexProjectId))
                .execute();
    }

    @Transactional
    public void deleteAllByAlexTestId(Long alexTestId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.ALEX_TEST_ID.eq(alexTestId))
                .execute();
    }

    @Transactional
    public void deleteAllByJiraTestId(Long jiraTestId) {
        dsl.delete(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .execute();
    }

    @Transactional
    public boolean existsByIssueId(Long jiraTestId) {
        return dsl.select().from(TEST_MAPPING)
                .where(TEST_MAPPING.JIRA_TEST_ID.eq(jiraTestId))
                .fetch()
                .size() > 0;
    }
}
