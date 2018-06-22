import axios from 'axios';
import {apiUrl} from '../../environments';

/**
 * Endpoints for managing projects in Jira.
 * IDs that are passed as arguments refer to entity IDs in Jira.
 */
export class JiraProjectApi {

  /** Constructor. */
  constructor() {

    /**
     * The URLs of the REST API.
     * @type {{projects: function(): string, project: function(number): string}}
     */
    this.urls = {
      projects: () => `${apiUrl}/jira/projects`,
      project: (projectId) => `${apiUrl}/jira/projects/${projectId}`
    };
  }

  /**
   * Get all projects in Jira.
   *
   * @return {AxiosPromise<any>}
   *    The HTTP promise.
   */
  find() {
    return axios.get(this.urls.projects());
  }

  /**
   * Get a single project in Jira.
   *
   * @param {number} projectId
   *    The ID of the project.
   * @return {AxiosPromise<any>}
   *    The HTTP promise
   */
  findOne(projectId) {
    return axios.get(this.urls.project(projectId));
  }
}

/**
 * An instance of the API service.
 * @type {JiraProjectApi}
 */
export const jiraProjectApi = new JiraProjectApi();
