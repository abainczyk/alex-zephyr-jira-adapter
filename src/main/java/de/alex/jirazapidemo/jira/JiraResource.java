package de.alex.jirazapidemo.jira;

import de.alex.jirazapidemo.api.settings.SettingsService;
import de.alex.jirazapidemo.db.h2.tables.pojos.Settings;
import org.glassfish.jersey.internal.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@RestController
public class JiraResource {

    @Autowired
    protected SettingsService settingsService;

    @Autowired
    protected JiraEndpoints jiraEndpoints;

    /** The HTTP client. */
    protected final Client client = ClientBuilder.newClient();

    /**
     * Returns the content for the HTTP "Authorization" header for HTTP Basic auth "username:password".
     *
     * @return The base64 encoded credentials.
     */
    public String auth() {
        final Settings settings = settingsService.getJiraSettings();
        final String credentials = settings.getUsername() + ":" + settings.getPassword();

        return "Basic " + Base64.encodeAsString(credentials.getBytes());
    }

}
