package de.alex.jirazapidemo.api.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource for synchronizing Jira and ALEX.
 */
@RestController
public class SyncResource {

    private final String RESOURCE_URL = "/rest/sync";

    @Autowired
    private SyncService syncService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    public ResponseEntity sync() {
        try {
            syncService.sync();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
