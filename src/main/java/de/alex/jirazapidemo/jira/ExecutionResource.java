package de.alex.jirazapidemo.jira;

import de.alex.jirazapidemo.utils.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
public class ExecutionResource {

    @Autowired
    private ExecutionService executionService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/rest/executions"
    )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity execute(@RequestBody ExecutionConfig config) {
        try {

            // TODO
            /*
              check if length of test steps in both are identical
              otherwise break and tell user to update
             */

            executionService.executeTest(config);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            final RestError error = new RestError(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/rest/executions/status"
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity getStauts() {
        return ResponseEntity.ok(executionService.getStatus());
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/rest/executions/abort"
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity abort() {
        executionService.abort();
        return ResponseEntity.ok().build();
    }
}
