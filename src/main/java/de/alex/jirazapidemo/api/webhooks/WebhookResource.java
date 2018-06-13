package de.alex.jirazapidemo.api.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.alex.jirazapidemo.api.events.IssueEventService;
import de.alex.jirazapidemo.api.events.ProjectEventService;
import de.alex.jirazapidemo.api.projectmappings.ProjectMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.IssueEvent;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
public class WebhookResource {

    private final String RESOURCE_URL = "/rest/wh";

    @Autowired
    private IssueEventService issueEventService;

    @Autowired
    private ProjectMappingService projectMappingService;

    @Autowired
    private ProjectEventService projectEventService;

    private final ObjectMapper objectMapper;

    public WebhookResource() {
        this.objectMapper = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/projects"
    )
    public ResponseEntity handleProjects(final @RequestBody String data) throws Exception {
        final ProjectEvent event = objectMapper.readValue(data, ProjectEvent.class);
        projectEventService.create(event);

        switch (event.getType()) {
            case "PROJECT_DELETED":
                projectMappingService.deleteByJiraProjectId(event.getProjectId());
                issueEventService.deleteByProjectId(event.getProjectId());
                break;
            default:
                break;
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/issues"
    )
    public ResponseEntity handleIssues(final @RequestBody String data) throws Exception {
        final IssueEvent event = objectMapper.readValue(data, IssueEvent.class);
        issueEventService.create(event);

        switch (event.getType()) {
            case "ISSUE_DELETED":
                issueEventService.deleteByProjectId(event.getProjectId());
                break;
            default:
                break;
        }

        return ResponseEntity.ok().build();
    }

}
