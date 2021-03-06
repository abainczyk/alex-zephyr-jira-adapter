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

package de.alex.alexforjira.api.messages;

import de.alex.alexforjira.db.h2.tables.pojos.IssueEvent;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for accessing messages in the database.
 */
@Service
public class MessagesService {

    private static final de.alex.alexforjira.db.h2.tables.IssueEvent ISSUE_EVENT
            = de.alex.alexforjira.db.h2.tables.IssueEvent.ISSUE_EVENT;

    private final DSLContext dsl;

    @Autowired
    public MessagesService(final DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Get all project related events.
     *
     * @param projectId
     *         The ID of the Jira project.
     * @return The events related to the project.
     */
    @Transactional
    public List<IssueEvent> getByProjectId(final Long projectId) {
        return dsl.select()
                .from(ISSUE_EVENT)
                .where(ISSUE_EVENT.PROJECT_ID.eq(projectId))
                .orderBy(ISSUE_EVENT.TIMESTAMP.desc())
                .fetch()
                .stream()
                .map(record -> record.into(new IssueEvent()))
                .collect(Collectors.toList());
    }

    /**
     * Add a project related event to the database.
     *
     * @param event
     *         The event to create.
     * @return The created event.
     */
    @Transactional
    public IssueEvent create(final IssueEvent event) {
        return dsl.insertInto(ISSUE_EVENT, ISSUE_EVENT.TYPE, ISSUE_EVENT.ISSUE_ID, ISSUE_EVENT.PROJECT_ID,
                              ISSUE_EVENT.ISSUE_SUMMARY, ISSUE_EVENT.TIMESTAMP)
                .values(event.getType(), event.getIssueId(), event.getProjectId(), event.getIssueSummary(),
                        event.getTimestamp())
                .returning()
                .fetchOne()
                .into(new IssueEvent());
    }

    /**
     * Delete all events by Jira project ID.
     *
     * @param projectId
     *         The ID of the Jira project.
     * @return The number of deleted events.
     */
    @Transactional
    public int deleteByProjectId(final Long projectId) {
        return dsl.delete(ISSUE_EVENT)
                .where(ISSUE_EVENT.PROJECT_ID.eq(projectId))
                .execute();
    }

    /**
     * Delete a single event.
     *
     * @param eventId
     *         The ID of the event.
     * @return The number of deleted events. Should be 1.
     */
    @Transactional
    public int deleteById(final int eventId) {
        return dsl.delete(ISSUE_EVENT)
                .where(ISSUE_EVENT.ID.eq(eventId))
                .execute();
    }
}
