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

import {testMappingApi} from '../apis/test-mapping-api';

export const testMappingsStore = {
  namespaced: true,
  state: {
    testMappings: []
  },
  mutations: {
    load(state, testMappings) {
      state.testMappings = testMappings;
    },
    add(state, testMapping) {
      state.testMappings.push(testMapping);
    },
    remove(state, mappingId) {
      state.testMappings = state.testMappings.filter(tm => tm.id !== mappingId);
    }
  },
  actions: {
    load({commit}, projectId) {
      return testMappingApi.find(projectId)
        .then(res => {
          commit('load', res.data);
          return res;
        });
    },
    create({commit}, testMapping) {
      return testMappingApi.create(testMapping.jiraProjectId, testMapping)
        .then(res => {
          commit('add', res.data);
          return res;
        });
    },
    remove({commit}, mapping) {
      return testMappingApi.delete(mapping.jiraProjectId, mapping.id)
        .then(res => {
          commit('remove', parseInt(mapping.id));
          return res;
        });
    }
  }
};
