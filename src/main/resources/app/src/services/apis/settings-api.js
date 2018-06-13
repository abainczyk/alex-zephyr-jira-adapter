import axios from 'axios';
import {apiUrl} from '../../environments';

export class SettingsApi {

  constructor() {
    this.url = () => `${apiUrl}/settings`;
  }

  get() {
    return axios.get(this.url());
  }

  update(settings) {
    return axios.put(this.url(), settings);
  }

}

export const settingsApi = new SettingsApi();
