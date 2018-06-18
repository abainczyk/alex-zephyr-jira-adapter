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

package de.alex.jirazapidemo.jira;

import de.alex.jirazapidemo.services.SettingsService;
import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@RestController
public class JiraResource {

    @Autowired
    protected JiraEndpoints jiraEndpoints;

    @Autowired
    protected SettingsService settingsService;

    /** The HTTP client. */
    protected final Client client = ClientBuilder.newClient();

    /**
     * Returns the content for the HTTP "Authorization" header for HTTP Basic auth "username:password".
     *
     * @return The base64 encoded credentials.
     */
    public String auth() {
        final String credentials = settingsService.getJiraUsername() + ":" + settingsService.getJiraPassword();

        return "Basic " + Base64.encodeAsString(credentials.getBytes());
    }

}
