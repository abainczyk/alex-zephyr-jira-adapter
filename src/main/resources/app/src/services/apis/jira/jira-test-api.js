import axios from 'axios';
import {apiUrl} from '../../../environments';

/**
 * Endpoints for managing tests in Jira.
 * IDs that are passed as arguments refer to entity IDs in Jira.
 */
export class JiraTestApi {

  /** Constructor. */
  constructor() {

    /**
     * The URLs of the REST API.
     * @type {{tests: function(*): string, test: function(*, *): string}}
     */
    this.urls = {
      tests: (projectId) => `${apiUrl}/jira/projects/${projectId}/tests`,
      test: (projectId, testId) => `${apiUrl}/jira/projects/${projectId}/tests/${testId}`,
      update: (projectId, testId) => `${apiUrl}/jira/projects/${projectId}/tests/${testId}/update`
    };
  }

  /**
   * Get all tests from Jira.
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
   * Get a single test from Jira.
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

  update(projectId, testId) {
    return axios.post(this.urls.update(projectId, testId), {});
  }
}

/**
 * An instance of the API service.
 * @type {JiraTestApi}
 */
export const jiraTestApi = new JiraTestApi();
