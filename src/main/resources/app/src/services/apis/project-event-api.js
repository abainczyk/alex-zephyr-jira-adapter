import axios from 'axios/index';
import {apiUrl} from '../../environments';

export class ProjectEventApi {

  constructor() {
    this.url = `${apiUrl}/events/projects`;
  }

  find() {
    return axios.get(this.url);
  }

}

export const projectEventApi = new ProjectEventApi();
