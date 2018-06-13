package de.alex.jirazapidemo.api.events;

import de.alex.jirazapidemo.db.h2.tables.pojos.IssueEvent;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventResource {

    private final String RESOURCE_URL = "/rest/events/projects";

    @Autowired
    private IssueEventService issueEventService;

    @Autowired
    private ProjectEventService projectEventService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    private ResponseEntity<List<ProjectEvent>> getProjectEvents() {
        return ResponseEntity.ok(projectEventService.get());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}/issues"
    )
    private ResponseEntity<List<IssueEvent>> getIssueEvents(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueEventService.getByProjectId(projectId));
    }

}
