import {issueEventApi} from '../apis/issue-event-api';

export const messagesStore = {
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
      return issueEventApi.remove(message.projectId, message.id)
        .then(() => commit('remove', message));
    }
  }
};
