<template>
  <div>

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

    <div class="message-wrapper d-flex" v-if="status != null && status.errors">
      <div class="message-box rounded p-4">
        <h6>Please fix the following issues in order to continue</h6>
        <hr>

        <div class="alert alert-danger" v-if="!status.jira.connected">
          You are not connected to Jira.
          Eventually you specified the wrong URL in the configuration or Jira is temporarily not available.
        </div>
        <div class="alert alert-danger" v-if="!status.jira.authenticated">
          You are not authorized to Jira.
        </div>
        <div class="alert alert-danger" v-if="!status.alex == null">
          You are not connected to ALEX.
          Eventually you specified the wrong URL in the configuration or ALEX is temporarily not available.
        </div>
        <div class="alert alert-danger" v-if="!status.alex.authenticated">
          You are not authorized to ALEX.
        </div>
        <div class="alert alert-danger" v-if="!status.zapi.errorId != null">
          There are problems with the <strong>ZAPI</strong> add-on in Jira.
          {{status.zapi.errorDesc}}.
        </div>

        <hr>
        <div class="text-right">
          <button class="btn btn-sm btn-primary" v-if="!status.loading" @click="checkStatus">
            Check again
          </button>
          <button class="btn btn-sm btn-primary" v-else disabled>
            <font-awesome-icon icon="sync" spin></font-awesome-icon>
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>

  /**
   * Component that displays the connection and authorization status. If there is an error, e.g. the connection to Jira
   * or ALEX cannot be established, a popup displays the errors.
   */
  export default {
    name: 'afj-status',
    computed: {
      status() {
        return this.$store.state.status.status;
      }
    },
    methods: {

      /** Check the status manually. */
      checkStatus() {
        if (!this.status.loading) {
          this.$store.dispatch('status/load');
        }
      }
    }
  };
</script>

<style scoped lang="scss">
  .status-bar {
    background: #33373c;
    color: #f1f1f1;
  }

  .message-wrapper {
    position: absolute;
    width: 100%;
    overflow: hidden;
    bottom: 0;
    top: 35px;
    background: rgba(51, 55, 60, 0.85);
    z-index: 9999;

    .message-box {
      margin: auto;
      align-self: center;
      max-width: 600px;
      background: #fff;
      box-shadow: 0 2px 10px rgba(0, 0, 0, .2);
    }
  }
</style>
