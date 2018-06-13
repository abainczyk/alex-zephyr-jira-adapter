import axios from 'axios';
import {apiUrl} from '../../../environments';

/**
 * Endpoints for managing versions in Jira.
 * IDs that are passed as arguments refer to entity IDs in Jira.
 */
export class JiraVersionApi {

  /** Constructor. */
  constructor() {

    /**
     * The URLs of the REST API.
     * @type {{versions: function(*): string}}
     */
    this.urls = {
      versions: (projectId) => `${apiUrl}/jira/projects/${projectId}/versions`
    };
  }

  /**
   * Get all versions in Jira.
   *
   * @param {number} projectId
   *    The ID of the project.
   * @return {AxiosPromise<any>}
   *    The HTTP promise.
   */
  find(projectId) {
    return axios.get(this.urls.versions(projectId));
  }
}

/**
 * An instance of the API service.
 * @type {JiraVersionApi}
 */
export const jiraVersionApi = new JiraVersionApi();
