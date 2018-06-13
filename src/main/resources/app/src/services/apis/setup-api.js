import axios from 'axios';
import {apiUrl} from '../../environments';

export class SetupApi {

  constructor() {
    this.url = (step) => `${apiUrl}/setup/steps/${step}`;
  }

  get() {
    return axios.get(`${apiUrl}/setup`);
  }

  setup(data) {
    return axios.post(`${apiUrl}/setup`, data);
  }

  step1(data) {
    return axios.post(this.url(1), data);
  }

  step2(data) {
    return axios.post(this.url(2), data);
  }

}

export const setupApi = new SetupApi();
