import axios from 'axios';
import {apiUrl} from '../../../environments';

export class JiraCyclesApi {

  constructor() {
    this.url = `${apiUrl}/jira/cycles`;
  }

  findAll(projectId) {
    return axios.get(`${this.url}?projectId=${projectId}`);
  }

  getFolders(cycleId, projectId, versionId) {
    return axios.get(`${this.url}/${cycleId}/folders?projectId=${projectId}&versionId=${versionId}`);
  }

  getTests(cycleId, folderId, projectId, versionId) {
    return axios.get(`${this.url}/${cycleId}/folders/${folderId}/tests?projectId=${projectId}&versionId=${versionId}`);
  }

  executeCycle(cycleId, config) {
    return axios.post(`${this.url}/${cycleId}/execute`, config);
  }

  executeFolder(cycleId, folderId, config) {
    return axios.post(`${this.url}/${cycleId}/folders/${folderId}/execute`, config);
  }
}

export const jiraCyclesApi = new JiraCyclesApi();
