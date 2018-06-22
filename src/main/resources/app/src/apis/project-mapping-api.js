import axios from 'axios';
import {apiUrl} from '../environments';

export class ProjectMappingApi {

  constructor() {
    this.url = (projectId) => `${apiUrl}/projects/${projectId}/mappings`;
  }

  find() {
    return axios.get(`${apiUrl}/projectMappings`);
  }

  findOne(projectId) {
    return axios.get(this.url(projectId));
  }

  delete(projectId) {
    return axios.delete(this.url(projectId), null);
  }

  create(projectId, mapping) {
    return axios.post(this.url(projectId), mapping);
  }

}

export const projectMappingApi = new ProjectMappingApi();
