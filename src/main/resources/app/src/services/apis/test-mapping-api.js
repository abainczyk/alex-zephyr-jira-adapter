import {apiUrl} from '../../environments';
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
