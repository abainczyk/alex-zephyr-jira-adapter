import axios from 'axios/index';
import {apiUrl} from '../../environments';

export class IssueEventApi {

  constructor() {
    this.url = (projectId) => `${apiUrl}/events/projects/${projectId}/issues`;
  }

  find(projectId) {
    return axios.get(this.url(projectId));
  }
}

export const issueEventApi = new IssueEventApi();
