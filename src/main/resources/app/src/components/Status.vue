<template>
  <div class="status-bar py-2 text-right" v-if="status != null">
    <div class="container">
      <span class="status-pill mr-3">
        Jira
        <span class="badge badge-success mr-1" v-if="status.jira.connected">Connected</span>
        <span class="badge badge-danger mr-1" v-else>Disconnected</span>

        <span class="badge badge-success" v-if="status.jira.authenticated">Authenticated</span>
        <span class="badge badge-danger" v-else>Not authenticated</span>
      </span>

      <span class="status-pill mr-3">
        ZAPI
        <span class="badge badge-success" v-if="status.zapi.status === 'ENABLED'">Enabled</span>
        <span class="badge badge-danger" v-else>Disabled</span>
      </span>

      <span class="status-pill">
        ALEX
        <span class="badge badge-success mr-1" v-if="status.alex != null">Connected</span>
        <span class="badge badge-danger mr-1" v-else>Disconnected</span>

        <span class="badge badge-success" v-if="status.alex.authenticated">Authenticated</span>
        <span class="badge badge-danger" v-else>Not authenticated</span>
        </span>
    </div>
  </div>
</template>

<script>
  import axios from 'axios';
  import {apiUrl} from '../environments';

  /**
   * Component that displays the connection and authorization status.
   * Polls for the connection status every 30 seconds.
   */
  export default {
    name: 'jzd-status',
    data() {
      return {

        /**
         * The status that indicates the connection status to ALEX, Jira and the ZAPI plugin.
         * @type {?Object}
         */
        status: null
      };
    },
    created() {
      this.getStatus();
      window.setInterval(() => this.getStatus, 30000);
    },
    methods: {

      /**
       * Get the status.
       */
      getStatus() {
        axios.get(`${apiUrl}/status`)
          .then(res => this.status = res.data)
          .catch(console.error);
      }
    }
  };
</script>

<style scoped>
  .status-bar {
    background: #33373c;
    color: #f1f1f1;
  }
</style>
