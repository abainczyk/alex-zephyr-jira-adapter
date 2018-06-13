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
