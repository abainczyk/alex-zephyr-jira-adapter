package de.alex.jirazapidemo.jira.versions;

import de.alex.jirazapidemo.jira.JiraResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
public class JiraVersionResource extends JiraResource {

    private final String RESOURCE_URL = "/rest/jira/projects/{projectId}/versions";

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity getTestsByProjectId(@PathVariable("projectId") String projectId) {
        final Response response = jiraEndpoints.versions(projectId).get();

        return ResponseEntity.status(response.getStatus())
                .body(response.readEntity(String.class));
    }

}
