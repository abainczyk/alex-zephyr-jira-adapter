package de.alex.jirazapidemo.api.setup;

import de.alex.jirazapidemo.db.h2.tables.pojos.Settings;

public class SetupData {

    private Settings alexSettings;

    private Settings jiraSettings;

    public Settings getAlexSettings() {
        return alexSettings;
    }

    public void setAlexSettings(Settings alexSettings) {
        this.alexSettings = alexSettings;
    }

    public Settings getJiraSettings() {
        return jiraSettings;
    }

    public void setJiraSettings(Settings jiraSettings) {
        this.jiraSettings = jiraSettings;
    }

}
