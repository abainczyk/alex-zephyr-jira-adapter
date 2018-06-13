import axios from 'axios';
import {apiUrl} from '../../environments';

export class SyncApi {

  constructor() {
    this.url = `${apiUrl}/sync`;
  }

  sync() {
    return axios.post(this.url, {});
  }

}

export const syncApi = new SyncApi();
