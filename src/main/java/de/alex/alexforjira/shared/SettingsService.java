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

package de.alex.alexforjira.shared;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/** Utility class that allows access to the properties in the config file. */
@Service
public class SettingsService {

    /** The properties from the config file. */
    private Properties properties;

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    public String getAppUrl() {
        return properties.getProperty("app.url");
    }

    public List<Long> getAllowedJiraProjectIds() {
        final String ids = properties.getProperty("jira.allowed-project-ids");
        return Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
    }

    public String getJiraUsername() {
        return properties.getProperty("jira.username");
    }

    public String getJiraPassword() {
        return properties.getProperty("jira.password");
    }

    public String getJiraUrl() {
        return properties.getProperty("jira.url");
    }

    public String getAlexEmail() {
        return properties.getProperty("alex.email");
    }

    public String getAlexPassword() {
        return properties.getProperty("alex.password");
    }

    public String getAlexUrl() {
        return properties.getProperty("alex.url");
    }

    public String getBrowserName() {
        return properties.getProperty("app.browser.name");
    }

    public int getBrowserWidth() {
        return Integer.valueOf(properties.getProperty("app.browser.width"));
    }

    public int getBrowserHeight() {
        return Integer.valueOf(properties.getProperty("app.browser.height"));
    }

    public boolean getBrowserHeadless() {
        return Boolean.valueOf(properties.getProperty("app.browser.headless"));
    }

    /**
     * Check if the properties are valid.
     *
     * @return If all properties are valid.
     */
    public boolean isValid() {
        return !getJiraUsername().trim().equals("")
                && !getJiraUrl().trim().equals("")
                && !getAlexEmail().trim().equals("")
                && !getAlexUrl().trim().equals("")
                && !getAlexPassword().trim().equals("")
                && !getAppUrl().trim().equals("")
                && isBrowserValid();
    }

    private boolean isBrowserValid() {
        return !getBrowserName().trim().equals("")
                && getBrowserWidth() > 0
                && getBrowserHeight() > 0;
    }
}
