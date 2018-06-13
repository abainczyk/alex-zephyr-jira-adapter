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

package de.alex.jirazapidemo.api.setup;

import de.alex.jirazapidemo.alex.entities.AlexUserLogin;
import de.alex.jirazapidemo.api.settings.SettingsService;
import de.alex.jirazapidemo.db.h2.tables.pojos.Settings;
import de.alex.jirazapidemo.jira.JiraResource;
import org.apache.commons.validator.routines.UrlValidator;
import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
public class SetupResource {

    private final String RESOURCE_URL = "/rest/setup";

    @Autowired
    private SettingsService settingsService;

    /** The HTTP client. */
    private final Client client;

    /** Constructor. */
    public SetupResource() {
        this.client = ClientBuilder.newClient();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity get() {
        final boolean jira = settingsService.getByAppName("jira") != null;
        final boolean alex = settingsService.getByAppName("alex") != null;

        final String data = String.format("{\"jira\": %b, \"alex\": %b}", jira, alex);

        return ResponseEntity.ok(data);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/steps/1"
    )
    public ResponseEntity step1(@RequestBody final Settings settings) {
        // TODO assert that url is not malformed

        final String url = withoutTrailingSlash(settings.getUri());

        final String credentials = settings.getUsername() + ":" + settings.getPassword();
        final String auth = "Basic " + Base64.encodeAsString(credentials.getBytes());

        // try to get the profile of the user
        final Response response = client.target(url + "/rest/api/2/myself")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", auth)
                .get();

        // TODO: check if user is admin

        // TODO: check if zapi module is installed

        if (response.getStatus() == 200) {
            return ResponseEntity.ok(String.format("{\"status\": %d}", response.getStatus()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/steps/2"
    )
    public ResponseEntity step2(@RequestBody final Settings settings) {
        // TODO assert that url is not malformed

        final String url = withoutTrailingSlash(settings.getUri());

        final AlexUserLogin login = new AlexUserLogin(settings.getUsername(), settings.getPassword());
        final Response response = client.target(url + "/rest/users/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(login));

        if (response.getStatus() == 200) {
            return ResponseEntity.ok(String.format("{\"status\": %d}", response.getStatus()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL
    )
    public ResponseEntity setup(@RequestBody final SetupData setupData) {
        settingsService.create(setupData.getJiraSettings());
        settingsService.create(setupData.getAlexSettings());

        return ResponseEntity.ok().build();
    }

    /**
     * Removes the trailing slash in a URL.
     *
     * @param url
     *         The URL.
     * @return The URL without trailing slash.
     */
    private String withoutTrailingSlash(final String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

}
