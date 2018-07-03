/*
 * Copyright 2018 Alexander Bainczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import axios from 'axios';
import {apiUrl} from '../environments';

/**
 * The user API.
 */
class UserApi {

  /**
   * Get all users.
   *
   * @return {AxiosPromise<any>}
   */
  find() {
    return axios.get(`${apiUrl}/admin/users`);
  }

  /**
   * Create a new user.
   *
   * @param {Object} user The user to create.
   * @return {AxiosPromise<any>}
   */
  create(user) {
    return axios.post(`${apiUrl}/admin/users`, user);
  }

  /**
   * Update a user.
   *
   * @param {Object} user The updated user instance.
   * @return {AxiosPromise<any>}
   */
  update(user) {
    return axios.put(`${apiUrl}/admin/users/${user.id}`, user);
  }

  /**
   * Deletes a user.
   *
   * @param {number} userId The ID of the user.
   * @return {AxiosPromise}
   */
  remove(userId) {
    return axios.delete(`${apiUrl}/admin/users/${userId}`);
  }

  /**
   * Updates the password of a user.
   *
   * @param {number} userId The ID of the user.
   * @param {string} password The new unencoded password.
   * @return {AxiosPromise<any>}
   */
  setPassword(userId, password) {
    return axios.put(`${apiUrl}/admin/users/${userId}/setPassword`, {password});
  }

  /**
   * Check if the user uses correct credentials.
   *
   * @param {Object} user The email / password combination
   * @return {AxiosPromise<any>}
   */
  signIn(user) {
    return axios.post(`${apiUrl}/users/signIn`, user);
  }
}

/**
 * An instance of the API.
 * @type {UserApi}
 */
export const userApi = new UserApi();
