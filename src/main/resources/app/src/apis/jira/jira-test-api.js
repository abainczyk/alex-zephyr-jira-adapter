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

import axios from 'axios';
import {apiUrl} from '../../environments';

/**
 * Endpoints for managing tests in Jira.
 * IDs that are passed as arguments refer to entity IDs in Jira.
 */
export class JiraTestApi {

  /**
   * Get all tests from Jira.
   *
   * @param {number} projectId The ID of the project.
   *
   * @return {AxiosPromise<any>}
   */
  find(projectId) {
    return axios.get(`${apiUrl}/jira/projects/${projectId}/tests`);
  }

  /**
   * Execute a test in ALEX.
   *
   * @param {number} projectId The ID of the project in Jira.
   * @param {number} testId The ID of the test in Jira.
   * @param {Object} config The configuration to use for the execution.
   *
   * @return {AxiosPromise<any>}
   */
  execute(projectId, testId, config) {
    return axios.post(`${apiUrl}/jira/projects/${projectId}/tests/${testId}/execute`, config);
  }

  /**
   * Generate test steps in Jira from the ALEX test.
   *
   * @param {number} projectId The ID of the project in Jira.
   * @param {number} testId The ID of the test in Jira.
   *
   * @return {AxiosPromise<any>}
   */
  update(projectId, testId) {
    return axios.post(`${apiUrl}/jira/projects/${projectId}/tests/${testId}/update`, {});
  }
}

/**
 * An instance of the API service.
 * @type {JiraTestApi}
 */
export const jiraTestApi = new JiraTestApi();
