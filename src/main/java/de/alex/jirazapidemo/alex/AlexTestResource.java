package de.alex.jirazapidemo.alex;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
public class AlexTestResource extends AlexResource {

    private final String RESOURCE_URL = "/rest/alex/projects/{projectId}/tests";

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    private ResponseEntity getAll(@PathVariable("projectId") Long projectId) {
        final Response response = alexEndpoints.testCases(projectId).get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/{testId}"
    )
    private ResponseEntity get(@PathVariable("projectId") Long projectId, @PathVariable("testId") Long testId) {
        final Response response = alexEndpoints.test(projectId, testId).get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL + "/root"
    )
    private ResponseEntity getRoot(@PathVariable("projectId") Long projectId) {
        final Response response = alexEndpoints.rootTest(projectId).get();

        return ResponseEntity
                .status(response.getStatus())
                .body(response.readEntity(String.class));
    }
}
