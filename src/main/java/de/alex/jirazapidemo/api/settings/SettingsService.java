package de.alex.jirazapidemo.api.settings;

import de.alex.jirazapidemo.db.h2.tables.pojos.Settings;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private DSLContext dsl;

    private Settings alexSettings = null;

    private Settings jiraSettings = null;

    private static final de.alex.jirazapidemo.db.h2.tables.Settings SETTINGS
            = de.alex.jirazapidemo.db.h2.tables.Settings.SETTINGS;

    @Transactional
    public List<Settings> get() {
        final List<Settings> settings = new ArrayList<>();

        dsl.select()
                .from(SETTINGS)
                .fetch()
                .forEach(record -> settings.add(record.into(new Settings())));

        return settings;
    }

    @Transactional
    public Settings getByAppName(String appName) {
        final Record record = dsl.select()
                .from(SETTINGS)
                .where(SETTINGS.APP_ID.eq(appName))
                .fetchOne();
        return record == null ? null : record.into(new Settings());
    }

    @Transactional
    public Settings create(de.alex.jirazapidemo.db.h2.tables.pojos.Settings settings) {
        final Record record = dsl.insertInto(SETTINGS, SETTINGS.APP_ID, SETTINGS.URI, SETTINGS.USERNAME,
                                             SETTINGS.PASSWORD)
                .values(settings.getAppId(), settings.getUri(), settings.getUsername(), settings.getPassword())
                .returning()
                .fetchOne();

        final Settings createdSettings = record.into(new Settings());
        if (settings.getAppId().equals(SettingsResource.ALEX_APP_ID)) {
            alexSettings = settings;
        } else if (settings.getAppId().equals(SettingsResource.JIRA_APP_ID)) {
            jiraSettings = settings;
        }
        return createdSettings;
    }

    @Transactional
    public void update(de.alex.jirazapidemo.db.h2.tables.pojos.Settings settings) {
        dsl.update(SETTINGS)
                .set(SETTINGS.URI, settings.getUri())
                .set(SETTINGS.USERNAME, settings.getUsername())
                .set(SETTINGS.PASSWORD, settings.getPassword())
                .where(SETTINGS.APP_ID.eq(settings.getAppId()))
                .execute();

        if (settings.getAppId().equals("alex")) {
            alexSettings = settings;
        } else if (settings.getAppId().equals("jira")) {
            jiraSettings = settings;
        }
    }

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
