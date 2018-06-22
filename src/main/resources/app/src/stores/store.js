import Vue from 'vue';
import Vuex from 'vuex';
import {jiraProjectApi} from '../apis/jira/jira-project-api';
import {jiraTestApi} from '../apis/jira/jira-test-api';
import {projectMappingApi} from '../apis/project-mapping-api';
import {testMappingApi} from '../apis/test-mapping-api';
import {issueEventApi} from '../apis/issue-event-api';
import axios from 'axios';
import {apiUrl} from '../environments';

Vue.use(Vuex);

const jiraStore = {
  namespaced: true,
  modules: {
    projects: {
      namespaced: true,
      state: {
        projects: []
      },
      mutations: {
        load(state, projects) {
          state.projects = projects;
        }
      },
      actions: {
        load({commit}) {
          return jiraProjectApi.find()
            .then(res => {
              commit('load', res.data);
              return res;
            });
        }
      },
      getters: {}
    },
    tests: {
      namespaced: true,
      state: {
        tests: []
      },
      mutations: {
        load(state, res) {
          state.tests = res.issues;
        }
      },
      actions: {
        load({commit}, projectId) {
          return jiraTestApi.find(projectId)
            .then(res => {
              commit('load', res.data);
              return res;
            });
        }
      },
      getters: {}
    }
  }
};

const projectMappingsStore = {
  namespaced: true,
  state: {
    projectMappings: [],
    currentProjectMapping: null
  },
  mutations: {
    load(state, projectMappings) {
      state.projectMappings = projectMappings;
    },
    setCurrent(state, projectMapping) {
      state.currentProjectMapping = projectMapping;
    },
    remove(state, jiraProjectId) {
      state.projectMappings = state.projectMappings.filter(pm => pm.jiraProjectId !== jiraProjectId);
    },
    add(state, projectMapping) {
      state.projectMappings.push(projectMapping);
    }
  },
  actions: {
    load({commit}) {
      return projectMappingApi.find()
        .then(res => {
          commit('load', res.data);
          return res;
        });
    },
    remove({commit}, jiraProjectId) {
      return projectMappingApi.deleteOneByJiraProjectId(jiraProjectId)
        .then(res => {
          commit('remove', jiraProjectId);
          return res;
        });
    },
    create({commit}, projectMapping) {
      return projectMappingApi.create(projectMapping)
        .then(res => {
          commit('add', res.data);
          return res;
        });
    }
  },
  getters: {}
};

const testMappingsStore = {
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
    remove(state, jiraTestId) {
      state.testMappings = state.testMappings.filter(tm => tm.jiraTestId !== jiraTestId);
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
      return testMappingApi.create(testMapping.jiraProjectId, testMapping.jiraTestId, testMapping)
        .then(res => {
          commit('add', res.data);
          return res;
        });
    },
    remove({commit}, data) {
      return testMappingApi.delete(data.jiraProjectId, data.jiraTestId)
        .then(res => {
          commit('remove', parseInt(data.jiraTestId));
          return res;
        });
    }
  },
  getters: {}
};

const messagesStore = {
  namespaced: true,
  state: {
    messages: []
  },
  mutations: {
    load(state, messages) {
      state.messages = messages;
    },
    remove(state, message) {
      state.messages = state.messages.filter(m => m.id !== message.id);
    }
  },
  actions: {
    load({commit}, projectId) {
      return issueEventApi.find(projectId)
        .then(res => {
          commit('load', res.data);
          return res;
        });
    },
    remove({commit}, message) {
      return this.issueEventApi.remove(message.projectId, message.id)
        .then(() => commit('remove', message));
    }
  },
  getters: {}
};

const statusStore = {
  namespaced: true,
  state: {
    loading: false,
    status: null
  },
  mutations: {
    load(state, status) {
      state.status = status;
      state.loading = false;
    },
    setLoading(state, loading) {
      state.loading = loading;
    }
  },
  actions: {
    load({commit}) {
      commit('setLoading', true);
      axios.get(`${apiUrl}/status`)
        .then(res => {
          commit('load', res.data);
        });
    }
  },
  getters: {}
};

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

