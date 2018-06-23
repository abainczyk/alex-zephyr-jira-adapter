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
 * API for cycles in Jira.
 */
export class JiraCyclesApi {

  /**
   * Get all cycles in a project.
   *
   * @param {number} projectId The ID of the project.
   *
   * @return {AxiosPromise<any>}
   */
  find(projectId) {
    return axios.get(`${apiUrl}/jira/cycles?projectId=${projectId}`);
  }

  /**
   * Get all folders in a cycle.
   *
   * @param {number} projectId The ID of the project.
   * @param {number} versionId The ID of the version.
   * @param {number} cycleId The ID of the cycle.
   *
   * @return {AxiosPromise<any>}
   */
  getFolders(projectId, versionId, cycleId) {
    return axios.get(`${apiUrl}/jira/cycles/${cycleId}/folders?projectId=${projectId}&versionId=${versionId}`);
  }

  /**
   * Get all tests within a folder.
   *
   * @param {number} projectId The ID of the project.
   * @param {number} versionId The ID of the version.
   * @param {number} cycleId The ID of the cycle.
   * @param {number} folderId The ID of the folder.
   *
   * @return {AxiosPromise<any>}
   */
  getTests(projectId, versionId, cycleId, folderId) {
    return axios.get(`${apiUrl}/jira/cycles/${cycleId}/folders/${folderId}/tests?projectId=${projectId}&versionId=${versionId}`);
  }

  /**
   * Execute a cycle. That means executing all tests in all folders of a cycle.
   *
   * @param {number} cycleId The ID of the cycle.
   * @param {Object} config The config to use for the execution.
   *
   * @return {AxiosPromise<any>}
   */
  executeCycle(cycleId, config) {
    return axios.post(`${apiUrl}/jira/cycles/${cycleId}/execute`, config);
  }

  /**
   * Execute a folder. That means executing all tests in the folder.
   *
   * @param {number} cycleId The ID of the cycle.
   * @param {number} folderId The ID of the folder.
   * @param {number} config The config to use for the execution.
   *
   * @return {AxiosPromise<any>}
   */
  executeFolder(cycleId, folderId, config) {
    return axios.post(`${apiUrl}/jira/cycles/${cycleId}/folders/${folderId}/execute`, config);
  }
}

/**
 * An instance of the API.
 * @type {JiraCyclesApi}
 */
export const jiraCyclesApi = new JiraCyclesApi();
