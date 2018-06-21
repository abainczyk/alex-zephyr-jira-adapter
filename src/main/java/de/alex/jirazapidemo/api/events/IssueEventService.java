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

package de.alex.jirazapidemo.api.events;

import de.alex.jirazapidemo.db.h2.tables.pojos.IssueEvent;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueEventService {

    @Autowired
    private DSLContext dsl;

    private static final de.alex.jirazapidemo.db.h2.tables.IssueEvent ISSUE_EVENT
            = de.alex.jirazapidemo.db.h2.tables.IssueEvent.ISSUE_EVENT;

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

    @Transactional
    public void deleteByProjectId(final Long projectId) {
        dsl.delete(ISSUE_EVENT)
                .where(ISSUE_EVENT.PROJECT_ID.eq(projectId))
                .execute();
    }

}
