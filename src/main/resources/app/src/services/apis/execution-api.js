import axios from 'axios/index';
import {apiUrl} from '../../environments';

class ExecutionApi {

  constructor() {
    this.url = () => `${apiUrl}/executions`;
  }

  getStatus() {
    return axios.get(`${this.url()}/status`);
  }

  abort() {
    return axios.post(`${this.url()}/abort`, {});
  }
}

export const executionApi = new ExecutionApi();
