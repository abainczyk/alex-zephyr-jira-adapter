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
 * Endpoints for managing projects in Jira.
 * IDs that are passed as arguments refer to entity IDs in Jira.
 */
export class JiraProjectApi {

  /**
   * Get all projects in Jira.
   *
   * @return {AxiosPromise<any>}
   */
  find() {
    return axios.get(`${apiUrl}/jira/projects`);
  }

  /**
   * Get a single project in Jira.
   *
   * @param {number} projectId The ID of the project.
   *
   * @return {AxiosPromise<any>}
   */
  findOne(projectId) {
    return axios.get(`${apiUrl}/jira/projects/${projectId}`);
  }
}

/**
 * An instance of the API service.
 * @type {JiraProjectApi}
 */
export const jiraProjectApi = new JiraProjectApi();
