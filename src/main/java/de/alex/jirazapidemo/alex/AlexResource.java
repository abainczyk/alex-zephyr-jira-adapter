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

package de.alex.jirazapidemo.alex;

import de.alex.jirazapidemo.alex.entities.AlexJwt;
import de.alex.jirazapidemo.alex.entities.AlexUserLogin;
import de.alex.jirazapidemo.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

@Service
public class AlexResource {

    @Autowired
    protected AlexEndpoints alexEndpoints;

    @Autowired
    protected SettingsService settingsService;

    /** The HTTP client. */
    protected final Client client = ClientBuilder.newClient();

    /**
     * The cached authorization token. Since ALEX keeps auth tokens alive indefinitely, there is no need to refresh it.
     */
    private String token = null;

    /**
     * Get the "Authorization" header for ALEX.
     *
     * @return The "Authorization" header value.
     */
    public String auth() {
        if (token != null) {
            return token;
        }

        final AlexUserLogin login = new AlexUserLogin(
                settingsService.getAlexEmail(),
                settingsService.getAlexPassword()
        );

        final Response response = alexEndpoints.login()
                .post(Entity.json(login));

        token = "Bearer " + response.readEntity(AlexJwt.class).getToken();
        return token;
    }

}
