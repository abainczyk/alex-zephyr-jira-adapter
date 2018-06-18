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

import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectEvent;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectEventService {

    @Autowired
    private DSLContext dsl;

    private static final de.alex.jirazapidemo.db.h2.tables.ProjectEvent PROJECT_EVENT
            = de.alex.jirazapidemo.db.h2.tables.ProjectEvent.PROJECT_EVENT;

    @Transactional
    public List<ProjectEvent> get() {
        return dsl.select()
                .from(PROJECT_EVENT)
                .orderBy(PROJECT_EVENT.TIMESTAMP.desc())
                .fetch()
                .stream()
                .map(record -> record.into(new ProjectEvent()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectEvent create(ProjectEvent event) {
        return dsl.insertInto(PROJECT_EVENT, PROJECT_EVENT.TYPE, PROJECT_EVENT.PROJECT_ID, PROJECT_EVENT.TIMESTAMP)
                .values(event.getType(), event.getProjectId(), event.getTimestamp())
                .returning()
                .fetchOne()
                .into(new ProjectEvent());
    }

}
