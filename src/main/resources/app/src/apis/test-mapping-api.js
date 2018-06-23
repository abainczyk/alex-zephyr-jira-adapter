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

import {apiUrl} from '../environments';
import axios from 'axios/index';

/** API for test mappings. */
export class TestMappingApi {

  /**
   * Create a new test mapping.
   *
   * @param {number} projectId The ID of the Jira project.
   * @param {Object} mapping The test mapping.
   *
   * @return {AxiosPromise<any>}
   */
  create(projectId, mapping) {
    return axios.post(`${apiUrl}/projects/${projectId}/testMappings`, mapping);
  }

  /**
   * Get all test mappings.
   *
   * @param {number} projectId The ID of the project.
   *
   * @return {AxiosPromise<any>}
   */
  find(projectId) {
    return axios.get(`${apiUrl}/projects/${projectId}/testMappings`);
  }

  /**
   * Delete a test mapping.
   *
   * @param {number} projectId The ID of the Jira project.
   * @param {number} mappingId The ID of the test mapping.
   *
   * @return {AxiosPromise}
   */
  delete(projectId, mappingId) {
    return axios.delete(`${apiUrl}/projects/${projectId}/testMappings/${mappingId}`);
  }
}

/**
 * An instance of the API.
 * @type {TestMappingApi}
 */
export const testMappingApi = new TestMappingApi();
