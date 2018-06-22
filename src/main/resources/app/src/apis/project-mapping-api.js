import axios from 'axios';
import {apiUrl} from '../environments';

export class ProjectMappingApi {

  constructor() {
    this.url = () => `${apiUrl}/projectMappings`;
  }

  find() {
    return axios.get(this.url());
  }

  findOneByJiraProjectId(projectId) {
    return axios.get(`${this.url()}/byJiraProjectId/${projectId}`);
  }

  deleteOneByJiraProjectId(projectId) {
    return axios.delete(`${this.url()}/byJiraProjectId/${projectId}`, null);
  }

  create(mapping) {
    return axios.post(this.url(), mapping);
  }
}

export const projectMappingApi = new ProjectMappingApi();
