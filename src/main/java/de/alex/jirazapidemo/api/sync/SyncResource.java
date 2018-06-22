/*
 * Copyright 2018 Alexander Bainczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.alex.jirazapidemo.api.sync;

import de.alex.jirazapidemo.utils.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resource for synchronizing Jira and ALEX.
 */
@RestController
public class SyncResource {

    private static final Logger log = LoggerFactory.getLogger(SyncResource.class);

    private static final String RESOURCE_URL = "/rest/sync";

    private final SyncService syncService;

    @Autowired
    public SyncResource(SyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Synchronize tests and projects between ALEX and Jira.
     *
     * @return No content on success.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    public ResponseEntity sync() {
        log.info("Entering sync()");

        try {
            syncService.sync();
            log.info("Leaving sync() - success");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Leaving sync() - error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new RestError(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }
}
