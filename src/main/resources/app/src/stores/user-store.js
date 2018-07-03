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

import {userApi} from '../apis/user-api';

export const userStore = {
  namespaced: true,
  state: {

    /**
     * The current user.
     * @type {?Object}
     */
    currentUser: null,

    /**
     * All users.
     * @type {Object[]}
     */
    users: []
  },
  mutations: {

    /**
     * Set the loading status.
     *
     * @param {Object} state The current state.
     * @param {Object} user The current user.
     */
    setCurrentUser(state, user) {
      state.currentUser = user;
    },

    /**
     * Set the users in the state.
     *
     * @param {Object} state The state.
     * @param {Object[]} users The users to set.
     */
    setUsers(state, users) {
      state.users = users;
    },

    /**
     * Adds a new user to the state.
     *
     * @param {Object} state The state.
     * @param {Object} user The user to add.
     */
    addUser(state, user) {
      state.users.push(user);
    },

    /**
     * Removes a user from the state.
     *
     * @param {Object} state The state.
     * @param {number} userId The ID of the user to remove.
     */
    removeUser(state, userId) {
      state.users = state.users.filter(u => u.id !== userId);
    },

    /**
     * Updates a user in the state.
     *
     * @param {Object} state The state.
     * @param {Object} user The updated user. Should contain an ID.
     */
    updateUser(state, user) {
      state.users = state.users.filter(u => u.id !== user.id).concat(user);
    }
  },
  actions: {

    /**
     * Load all users.
     *
     * @param {Function} commit.
     * @return {*}
     */
    load({commit}) {
      return userApi.find()
        .then(res => commit('setUsers', res.data));
    },

    /**
     * Load the status.
     *
     * @param {Function} commit.
     * @param {Object} user The user to sign in.
     *
     * @return {Promise<AxiosResponse<any>>}
     */
    signIn({commit}, user) {
      return userApi.signIn(user)
        .then(res => {
          const signedInUser = res.data;
          const token = 'Basic ' + btoa(`${user.email}:${user.password}`);
          localStorage.setItem('user', JSON.stringify(signedInUser));
          localStorage.setItem('token', token);
          commit('setCurrentUser', signedInUser);
          return signedInUser;
        });
    },

    /**
     * Sign out the current user.
     *
     * @param {Function} commit.
     * @return {Promise<any>}
     */
    signOut({commit}) {
      return new Promise((resolve) => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        commit('setCurrentUser', null);
        commit('setUsers', []);
        resolve();
      });
    },

    /**
     * Creates a new user.
     *
     * @param {Function} commit
     * @param {Object} user The user to create.
     * @return {*}
     */
    create({commit}, user) {
      return userApi.create(user)
        .then(res => {
          commit('addUser', res.data);
          return res;
        });
    },

    remove({commit}, userId) {
      return userApi.remove(userId)
        .then(res => {
          commit('removeUser', userId);
          return res;
        });
    },

    update({commit}, user) {
      return userApi.update(user)
        .then(res => {
          commit('updateUser', res.data);
          return res;
        });
    }
  }
};
