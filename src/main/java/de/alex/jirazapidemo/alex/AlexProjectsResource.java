package de.alex.jirazapidemo.alex;

import de.alex.jirazapidemo.api.projectmappings.ProjectMappingService;
import de.alex.jirazapidemo.db.h2.tables.pojos.ProjectMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

@RestController
public class AlexProjectsResource extends AlexResource {

    private final String RESOURCE_URL = "/rest/alex/projects";

    @Autowired
    private ProjectMappingService projectMappingService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    private ResponseEntity create(@RequestBody String project) {
        final Response response = alexEndpoints.projects().post(Entity.json(project));

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    private ResponseEntity getAll() {
        final Response response = alexEndpoints.projects().get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{projectId}"
    )
    private ResponseEntity get(@PathVariable Long projectId) {
        final Response response = alexEndpoints.project(projectId).get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/byJiraProject/{projectId}"
    )
    private ResponseEntity getByJiraProjectId(@PathVariable Long projectId) {
        final ProjectMapping mapping = projectMappingService.getByJiraProjectId(projectId);
        if (mapping == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return get(mapping.getAlexProjectId());
    }

}
