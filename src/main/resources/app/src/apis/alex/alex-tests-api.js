import axios from 'axios/index';
import {apiUrl} from '../../environments';

/**
 * Endpoints for managing tests in ALEX.
 * IDs that are passed as arguments refer to entity IDs in ALEX.
 */
export class AlexTestsApi {

  /** Constructor. */
  constructor() {

    /**
     * The URLs of the REST API.
     * @type {{root: function(*): string}}
     */
    this.urls = {
      root: (projectId) => `${apiUrl}/alex/projects/${projectId}/tests/root`
    };
  }

  /**
   * Get the root test suite.
   *
   * @param {number} projectId
   *  The ID of the project.
   * @return {AxiosPromise<any>}
   *  The HTTP promise.
   */
  findRoot(projectId) {
    return axios.get(this.urls.root(projectId));
  }
}

/**
 * An instance of the API service.
 * @type {AlexTestsApi}
 */
export const alexTestsApi = new AlexTestsApi();
