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

    @Autowired
    private DSLContext dsl;

    @Autowired
    private TestMappingService testMappingService;

    private static final de.alex.jirazapidemo.db.h2.tables.ProjectMapping PROJECT_MAPPING
            = de.alex.jirazapidemo.db.h2.tables.ProjectMapping.PROJECT_MAPPING;

    @Transactional
    public ProjectMapping createOrUpdate(final ProjectMapping mapping) {
        final Optional<Integer> optional = dsl.selectCount()
                .from(PROJECT_MAPPING)
                .where(PROJECT_MAPPING.JIRA_PROJECT_ID.eq(mapping.getJiraProjectId()))
                .fetchOptionalInto(Integer.class);

        if (optional.isPresent()) {
            if (optional.get().equals(0)) {
                dsl.insertInto(PROJECT_MAPPING, PROJECT_MAPPING.ALEX_PROJECT_ID, PROJECT_MAPPING.JIRA_PROJECT_ID)
                        .values(mapping.getAlexProjectId(), mapping.getJiraProjectId())
                        .execute();
            } else {
                dsl.update(PROJECT_MAPPING)
                        .set(PROJECT_MAPPING.ALEX_PROJECT_ID, mapping.getAlexProjectId())
                        .where(PROJECT_MAPPING.JIRA_PROJECT_ID.eq(mapping.getJiraProjectId()))
                        .execute();
            }

            return getByJiraProjectId(mapping.getJiraProjectId());
        }

        return null;
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
