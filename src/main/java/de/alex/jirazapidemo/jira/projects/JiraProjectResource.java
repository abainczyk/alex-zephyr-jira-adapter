package de.alex.jirazapidemo.jira.projects;

import de.alex.jirazapidemo.jira.JiraEndpoints;
import de.alex.jirazapidemo.jira.JiraResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
public class JiraProjectResource extends JiraResource {

    private final String RESOURCE_URL = "/rest/jira/projects";

    @Autowired
    private JiraEndpoints jiraEndpoints;

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity getAll() {
        final Response response = jiraEndpoints.projects().get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}"
    )
    public ResponseEntity get(@PathVariable("projectId") String projectId) {
        final Response response = jiraEndpoints.project(projectId).get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

}
