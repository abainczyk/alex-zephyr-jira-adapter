package de.alex.jirazapidemo.alex;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
public class AlexStatusResource extends AlexResource {

    private static final String RESOURCE_URL = "/rest/alex/status";

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity status() {
        final Response response = client.target(alexEndpoints.url())
                .request(MediaType.APPLICATION_JSON)
                .get();

        return ResponseEntity.status(response.getStatus()).build();
    }

}
