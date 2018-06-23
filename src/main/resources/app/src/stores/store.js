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

import Vue from 'vue';
import Vuex from 'vuex';
import {jiraStore} from './jira-store';
import {projectMappingsStore} from './project-mappings-store';
import {testMappingsStore} from './test-mappings-store';
import {statusStore} from './status-store';
import {messagesStore} from './messages-store';

Vue.use(Vuex);

export const store = new Vuex.Store({
  modules: {
    projectMappings: projectMappingsStore,
    testMappings: testMappingsStore,
    messages: messagesStore,
    jira: jiraStore,
    status: statusStore
  }
});

store.dispatch('status/load');
window.setInterval(() => {
  store.dispatch('status/load');
}, 30000);

