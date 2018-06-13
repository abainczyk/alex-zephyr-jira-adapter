import axios from 'axios';
import {apiUrl} from '../../../environments';

/**
 * Endpoints for managing projects in ALEX.
 * IDs that are passed as arguments refer to entity IDs in ALEX.
 */
export class AlexProjectApi {

  /** Constructor. */
  constructor() {

    /**
     * The URLs of the REST API.
     * @type {{projects: function(): string, project: function(number): string}}
     */
    this.urls = {
      projects: () => `${apiUrl}/alex/projects`,
      project: (projectId) => `${apiUrl}/alex/projects/${projectId}`
    };
  }

  /**
   * Get all projects in ALEX.
   *
   * @return {AxiosPromise<any>}
   *    The HTTP promise.
   */
  find() {
    return axios.get(this.urls.projects());
  }

  /**
   * Get a single project by its ID.
   *
   * @param {number} projectId
   *    The ID of the project.
   * @return {AxiosPromise<any>}
   *    The HTTP promise.
   */
  findOne(projectId) {
    return axios.get(this.urls.project(projectId));
  }
}

/**
 * An instance of the API service.
 * @type {AlexProjectApi}
 */
export const alexProjectApi = new AlexProjectApi();
