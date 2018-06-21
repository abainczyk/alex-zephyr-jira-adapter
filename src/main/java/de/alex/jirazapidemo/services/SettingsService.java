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

package de.alex.jirazapidemo.services;

import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SettingsService {

    private Properties properties;

    public void setProperties(Properties properties) {
        this.properties = properties;
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

    public boolean isValid() {
        return !getJiraUsername().trim().equals("")
                && !getJiraUrl().trim().equals("")
                && !getAlexEmail().trim().equals("")
                && !getAlexUrl().trim().equals("")
                && !getAlexPassword().trim().equals("");
    }

}
