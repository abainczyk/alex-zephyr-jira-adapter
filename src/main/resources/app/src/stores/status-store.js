import {apiUrl} from '../environments';
import axios from 'axios/index';

export const statusStore = {
  namespaced: true,
  state: {

    /**
     * If the status is being loaded.
     * @type {boolean}
     */
    loading: false,

    /**
     * The status.
     * @type {?Object}
     */
    status: null
  },
  mutations: {

    /**
     * Load the status in the store.
     *
     * @param {Object} state The current state.
     * @param {Object} status The status.
     */
    load(state, status) {
      state.status = status;
      state.loading = false;
    },

    /**
     * Set the loading status.
     *
     * @param {Object} state The current state.
     * @param {boolean} loading If the status is being loaded.
     */
    setLoading(state, loading) {
      state.loading = loading;
    }
  },
  actions: {

    /**
     * Load the status.
     *
     * @param {Function} commit
     *
     * @return {Promise<AxiosResponse<any>>}
     */
    load({commit}) {
      commit('setLoading', true);
      return axios.get(`${apiUrl}/status`)
        .then(res => {
          commit('load', res.data);
        });
    }
  }
};
