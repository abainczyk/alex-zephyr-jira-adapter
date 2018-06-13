package de.alex.jirazapidemo.api.settings;

import de.alex.jirazapidemo.db.h2.tables.pojos.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SettingsResource {

    private final String RESOURCE_URL = "/rest/settings";

    public static final String JIRA_APP_ID = "jira";

    public static final String ALEX_APP_ID = "alex";

    @Autowired
    private SettingsService settingsService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = RESOURCE_URL
    )
    public ResponseEntity<Map<String, Settings>> get() {
        final Map<String, Settings> settingsMap = new HashMap<>();
        settingsService.get().forEach(settings -> settingsMap.put(settings.getAppId(), settings));
        return ResponseEntity.ok(settingsMap);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = RESOURCE_URL
    )
    public ResponseEntity<Map<String, Settings>> update(@RequestBody final Map<String, Settings> settingsMap) {
        settingsMap.forEach((name, settings) -> settingsService.update(settings));
        return ResponseEntity.ok(settingsMap);
    }

    @PostConstruct
    public void initSettings() {
        final Settings alexSettings = settingsService.getByAppName(ALEX_APP_ID);
        final Settings jiraSettings = settingsService.getByAppName(JIRA_APP_ID);

        if (alexSettings != null) {
            settingsService.setAlexSettings(alexSettings);
        }

        if (jiraSettings != null) {
            settingsService.setJiraSettings(jiraSettings);
        }
    }

}
