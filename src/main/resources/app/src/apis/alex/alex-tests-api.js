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
     * @type {{tests: function(number): string, test: function(number, number): string}}
     */
    this.urls = {
      tests: (projectId) => `${apiUrl}/alex/projects/${projectId}/tests`,
      test: (projectId, testId) => `${apiUrl}/alex/projects/${projectId}/tests/${testId}`,
      root: (projectId) => `${apiUrl}/alex/projects/${projectId}/tests/root`
    };
  }

  /**
   * Get all tests from ALEX.
   *
   * @param {number} projectId
   *    The ID of the project.
   * @return {AxiosPromise<any>}
   *    The HTTP promise.
   */
  find(projectId) {
    return axios.get(this.urls.tests(projectId));
  }

  /**
   * Get a single test from ALEX.
   *
   * @param {number} projectId
   *    The ID of the project.
   * @param {number} testId
   *    The ID of the test.
   * @return {AxiosPromise<any>}
   *    The HTTP promise.
   */
  findOne(projectId, testId) {
    return axios.get(this.urls.test(projectId, testId));
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
