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

import de.alex.alexforjira.api.jira.entities.JiraProject;
import de.alex.alexforjira.security.ProjectForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JiraUtils {

    private final SettingsService settingsService;

    @Autowired
    public JiraUtils(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public List<JiraProject> filterAllowedProjects(List<JiraProject> projects) {
        final List<Long> allowedProjectIds = settingsService.getAllowedJiraProjectIds();
        if (allowedProjectIds.isEmpty()) {
            return projects;
        } else {
            return projects.stream()
                    .filter(p -> allowedProjectIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }
    }

    public boolean isAllowedProjectId(Long id) {
        return settingsService.getAllowedJiraProjectIds().isEmpty() || settingsService.getAllowedJiraProjectIds().contains(id);
    }

    public void checkIfProjectIsAllowed(Long projectId) throws ProjectForbiddenException {
        if (!isAllowedProjectId(projectId)) {
            throw new ProjectForbiddenException("The project with the ID " + projectId + " may not be accessed.");
        }
    }
}
