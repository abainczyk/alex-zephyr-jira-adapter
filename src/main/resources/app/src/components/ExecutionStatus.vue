<template>
  <div v-if="status != null" class="d-flex align-items-baseline">
    <span class="badge badge-secondary p-2 mr-1" v-if="!status.active">Ready for testing</span>
    <div v-else>
      <span class="badge badge-primary p-2 mr-1">
        <font-awesome-icon icon="circle-notch" spin fixed-width class="mr-1"></font-awesome-icon>
        Testing in progress
      </span>
      <span class="badge badge-info p-2 mr-1">
        {{status.inQueue}} left
      </span>
    </div>
  </div>
</template>

<script>
  import {executionApi} from '../apis/execution-api';

  /** How often in ms we poll for the status of the test execution. */
  const INTERVAL_TIME = 3000;

  /**
   * Component that displays the current test execution progress.
   * Displays if and that testing is in progress and how many tests are left to execute.
   * Polls for the status every 3 seconds.
   */
  export default {
    name: 'afj-execution-status',
    data() {
      return {

        /**
         * The testing status.
         * @type {?Object}
         */
        status: null,

        /**
         * The interval handle.
         * @type {?number}
         */
        intervalHandle: null
      };
    },
    created() {
      this.getStatus();
      this.intervalHandle = window.setInterval(() => this.getStatus(), INTERVAL_TIME);
    },
    destroyed() {
      window.clearInterval(this.intervalHandle);
    },
    methods: {

      /** Get the testing status. */
      getStatus() {
        executionApi.getStatus()
          .then(res => this.status = res.data)
          .catch(console.error);
      },

      /** Abort the testing process. */
      abort() {
        executionApi.abort();
      }
    }
  };
</script>

<style scoped>
</style>
