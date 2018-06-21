import axios from 'axios/index';
import {apiUrl} from '../../environments';

export class IssueEventApi {

  constructor() {
    this.url = (projectId) => `${apiUrl}/events/projects/${projectId}/issues`;
  }

  find(projectId) {
    return axios.get(this.url(projectId));
  }

  remove(projectId, eventId) {
    return axios.delete(`${this.url(projectId)}/${eventId}`);
  }
}

export const issueEventApi = new IssueEventApi();
