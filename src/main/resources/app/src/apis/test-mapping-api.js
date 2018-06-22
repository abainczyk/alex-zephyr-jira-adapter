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

export class TestMappingApi {

  constructor() {
    this.url = (projectId, testId) => `${apiUrl}/projects/${projectId}/tests/${testId}/mappings`;
  }

  create(projectId, testId, mapping) {
    return axios.post(this.url(projectId, testId), mapping);
  }

  findOne(projectId, testId) {
    return axios.get(this.url(projectId, testId));
  }

  find(projectId) {
    return axios.get(`${apiUrl}/projects/${projectId}/testMappings`);
  }

  delete(projectId, testId) {
    return axios.delete(this.url(projectId, testId));
  }
}

export const testMappingApi = new TestMappingApi();
