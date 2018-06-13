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
