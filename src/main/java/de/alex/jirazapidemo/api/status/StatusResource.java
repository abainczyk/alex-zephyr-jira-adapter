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

package de.alex.jirazapidemo.api.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.alex.jirazapidemo.alex.AlexEndpoints;
import de.alex.jirazapidemo.alex.entities.AlexUserLogin;
import de.alex.jirazapidemo.jira.JiraEndpoints;
import de.alex.jirazapidemo.services.SettingsService;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
public class StatusResource {

    private static final String RESOURCE_URL = "/rest/status";

    private final JiraEndpoints jiraEndpoints;

    private final AlexEndpoints alexEndpoints;

    private final SettingsService settingsService;

    private final Client client;

    private final ObjectMapper objectMapper;

    @Autowired
    public StatusResource(JiraEndpoints jiraEndpoints,
                          AlexEndpoints alexEndpoints,
                          SettingsService settingsService) {
        this.jiraEndpoints = jiraEndpoints;
        this.alexEndpoints = alexEndpoints;
        this.settingsService = settingsService;

        this.objectMapper = new ObjectMapper();
        this.client = ClientBuilder.newClient()
                .property(ClientProperties.CONNECT_TIMEOUT, 3000)
                .property(ClientProperties.READ_TIMEOUT, 3000);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity get() throws Exception {
        boolean connectedToJira = false;
        boolean authenticatedInJira = false;
        boolean connectedToAlex = false;
        boolean authenticatedInAlex = false;

        // check if jira is reachable
        try {
            client.target(jiraEndpoints.url() + "/api/2/serverInfo")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            connectedToJira = true;
        } catch (Exception e) {
        }

        // check if authentication with jira works
        try {
            authenticatedInJira = client.target(jiraEndpoints.url() + "/api/2/user")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", jiraEndpoints.auth())
                    .get()
                    .getStatus() != 401;
        } catch (Exception e) {
        }

        // check if ALEX is reachable
        try {
            client.target(alexEndpoints.url()).request().get();
            connectedToAlex = true;
        } catch (Exception e) {
        }

        // check if authentication with ALEX works
        try {
            authenticatedInAlex = alexEndpoints.login()
                    .post(Entity.json(new AlexUserLogin(
                            settingsService.getAlexEmail(),
                            settingsService.getAlexPassword()
                    )))
                    .getStatus() == 200;
        } catch (Exception e) {
        }

        // check if the zapi add-on is enabled
        final Response response = client.target(jiraEndpoints.url() + "/zapi/latest/moduleInfo")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", jiraEndpoints.auth())
                .get();

        final JsonNode zapiStatus = objectMapper.readTree(response.readEntity(String.class));
        final boolean zapiInError = zapiStatus.has("errorId");

        final String data = "{"
                + "\"jira\":" + "{"
                + "\"connected\": " + connectedToJira + ", \"authenticated\": " + authenticatedInJira + "},"
                + "\"zapi\":" + zapiStatus.toString() + ","
                + "\"alex\":" + "{\"connected\": " + connectedToAlex + ", \"authenticated\": " + authenticatedInAlex + "},"
                + "\"errors\":" + (!connectedToJira || !authenticatedInJira || !connectedToAlex || !authenticatedInAlex || zapiInError)
                + "}";

        return ResponseEntity.ok(data);
    }

}
