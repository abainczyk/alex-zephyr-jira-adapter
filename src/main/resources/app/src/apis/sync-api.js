import axios from 'axios';
import {apiUrl} from '../environments';

/** API for synchronizing tests and projects between Jira and ALEX. */
export class SyncApi {

  /** Constructor. */
  constructor() {

    /**
     * The URL of the API endpoint.
     * @type {string}
     */
    this.url = `${apiUrl}/sync`;
  }

  /**
   * Sync tests and projects between Jira and ALEX.
   *
   * @return {AxiosPromise<any>}
   *    The HTTP promise.
   */
  sync() {
    return axios.post(this.url, {});
  }
}

/**
 * An instance of the API service.
 * @type {SyncApi}
 */
export const syncApi = new SyncApi();
