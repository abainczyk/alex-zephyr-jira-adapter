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

package de.alex.jirazapidemo;

import de.alex.jirazapidemo.alex.AlexEndpoints;
import de.alex.jirazapidemo.api.sync.SyncService;
import de.alex.jirazapidemo.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Entity;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/** Initialize the application. */
@Component
public class ServletInitializer extends SpringBootServletInitializer {

    @Autowired
    private SyncService syncService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private AlexEndpoints alexEndpoints;

    @Value("${app.configFile}")
    private String configFile;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AlexForJiraAdapter.class);
    }

    /** Sync state between ALEX and Jira. */
    @PostConstruct
    public void init() {
        sync();
        registerWebhooks();
    }

    private void registerWebhooks() {
        final String projectsWebhook = "{"
                + "\"url\":\"http://localhost:9000/rest/wh/alex/projects\""
                + ",\"name\":\"ALEX for Jira Adapter\""
                + ",\"events\":[\"PROJECT_DELETED\"]"
                + "}";
        alexEndpoints.webhooks().post(Entity.json(projectsWebhook));

        final String testsWebhook = "{"
                + "\"url\":\"http://localhost:9000/rest/wh/alex/tests\""
                + ",\"name\":\"ALEX for Jira Adapter\""
                + ",\"events\":[\"TEST_UPDATED\",\"TEST_DELETED\"]"
                + "}";
        alexEndpoints.webhooks().post(Entity.json(testsWebhook));
    }

    private void sync() {
        try {
            if (configFile == null || configFile.trim().equals("")) {
                throw new Exception("\nThe config file has not been specified\n");
            }

            final File file = new File(configFile);
            if (!file.exists()) {
                throw new Exception("\nThe config file cannot be found under the provided path.\n");
            }

            final Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            settingsService.setProperties(properties);

            if (!settingsService.isValid()) {
                throw new Exception("The configuration is not valid");
            }

            syncService.sync();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }
}
