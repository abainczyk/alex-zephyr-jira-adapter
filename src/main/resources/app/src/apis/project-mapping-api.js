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
import {apiUrl} from '../environments';

/**
 * The API for handling project mappings.
 */
export class ProjectMappingApi {

  /**
   * Get all project mappings.
   *
   * @return {AxiosPromise<any>}
   */
  find() {
    return axios.get(`${apiUrl}/projectMappings`);
  }

  /**
   * Get a project mapping by Jira project ID.
   *
   * @param {number} projectId The ID of the Jira project.
   *
   * @return {AxiosPromise<any>}
   */
  findOneByJiraProjectId(projectId) {
    return axios.get(`${apiUrl}/projectMappings/byJiraProjectId/${projectId}`);
  }

  /**
   * Delete a project mapping.
   *
   * @param {number} mappingId The ID of the mapping.
   *
   * @return {AxiosPromise}
   */
  delete(mappingId) {
    return axios.delete(`${apiUrl}/projectMappings/${mappingId}`, null);
  }

  /**
   * Create a new mapping between two projects.
   *
   * @param {Object} mapping The mapping.
   *
   * @return {AxiosPromise<any>}
   */
  create(mapping) {
    return axios.post(`${apiUrl}/projectMappings`, mapping);
  }
}

/**
 * An instance of the API.
 * @type {ProjectMappingApi}
 */
export const projectMappingApi = new ProjectMappingApi();
