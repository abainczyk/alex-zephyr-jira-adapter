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

import axios from 'axios/index';
import {apiUrl} from '../environments';

/**
 * The API for messages.
 */
export class IssueEventApi {

  /**
   * Get all available messages.
   *
   * @param {number} projectId The ID of the project.
   *
   * @return {AxiosPromise<any>}
   */
  find(projectId) {
    return axios.get(`${apiUrl}/messages/projects/${projectId}/issues`);
  }

  /**
   * Delete a message.
   *
   * @param {number} projectId The ID of the project.
   * @param {number} messageId The ID of the message.
   *
   * @return {AxiosPromise}
   */
  remove(projectId, messageId) {
    return axios.delete(`${apiUrl}/messages/projects/${projectId}/issues/${messageId}`);
  }
}

/**
 * An instance of the API.
 * @type {IssueEventApi}
 */
export const issueEventApi = new IssueEventApi();
