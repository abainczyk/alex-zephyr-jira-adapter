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

import {jiraTestApi} from '../apis/jira/jira-test-api';
import {jiraProjectApi} from '../apis/jira/jira-project-api';

/**
 * The store for Jira related data.
 * @type {Object}
 */
export const jiraStore = {
  namespaced: true,
  modules: {
    projects: {
      namespaced: true,
      state: {

        /**
         * The projects in Jira.
         * @type {Object[]}
         */
        projects: []
      },
      mutations: {

        /**
         * Set projects in the store.
         *
         * @param {Object} state The current state.
         * @param {Object[]} projects The Jira projects.
         */
        load(state, projects) {
          state.projects = projects;
        }
      },
      actions: {

        /**
         * Load all projects.
         *
         * @param {Function} commit
         *
         * @return {Promise<T>}
         */
        load({commit}) {
          return jiraProjectApi.find()
            .then(res => {
              commit('load', res.data);
              return res;
            });
        }
      }
    },
    tests: {
      namespaced: true,
      state: {

        /**
         * The tests in Jira.
         * @type {Object[]}
         */
        tests: []
      },
      mutations: {

        /**
         * Set tests in the store.
         *
         * @param {Object} state The current state.
         * @param {Object} res The tests.
         */
        load(state, res) {
          state.tests = res.issues;
        }
      },
      actions: {

        /**
         * Load all tests.
         *
         * @param {Function} commit
         * @param {number} projectId The ID of the project.
         *
         * @return {Promise<T>}
         */
        load({commit}, projectId) {
          return jiraTestApi.find(projectId)
            .then(res => {
              commit('load', res.data);
              return res;
            });
        }
      }
    }
  }
};
