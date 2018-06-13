package de.alex.jirazapidemo.api.projectmappings;

import de.alex.jirazapidemo.api.events.IssueEventService;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectMappingResource {

    private final String RESOURCE_URL = "/rest/projects/{projectId}/mappings";

    @Autowired
    private ProjectMappingService projectMappingService;

    @Autowired
    private IssueEventService issueEventService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity<ProjectMapping> get(@PathVariable("projectId") final Long projectId) {
        final ProjectMapping mapping = projectMappingService.getByJiraProjectId(projectId);
        return mapping == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(mapping);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    public ResponseEntity<ProjectMapping> create(@PathVariable("projectId") final Long projectId,
                                                 @RequestBody final ProjectMapping projectMapping) {
        final ProjectMapping mapping = projectMappingService.createOrUpdate(projectMapping);

        // TODO create webhooks in ALEX

        return ResponseEntity.ok(mapping);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = RESOURCE_URL
    )
    public ResponseEntity<ProjectMapping> delete(@PathVariable("projectId") final Long projectId) {
        projectMappingService.deleteByJiraProjectId(projectId);
        issueEventService.deleteByProjectId(projectId);

        // TODO delete webhooks in ALEX

        return ResponseEntity.noContent().build();
    }

}
