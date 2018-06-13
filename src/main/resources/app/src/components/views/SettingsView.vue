<template>
  <div class="mt-3">
    <h5>Settings</h5>
    <hr>

    <section class="mt-5">
      <h6 class="mt-5">Authentication</h6>
      <p class="text-muted">
        Update the authentication settings for Jira and ALEX
      </p>
      <hr>
      <div class="row" v-if="settings != null">
        <div class="col col-6">
          <h6>Jira</h6>
          <form>
            <div class="form-group">
              <input class="form-control" type="url" placeholder="URI" v-model="settings.jira.uri">
            </div>
            <div class="form-group">
              <input class="form-control" type="text" placeholder="Username" v-model="settings.jira.username">
            </div>
            <div class="form-group">
              <input
                  class="form-control"
                  type="password"
                  placeholder="Password"
                  v-model="settings.jira.password"
              >
            </div>
          </form>
        </div>
        <div class="col col-6">
          <h6>ALEX</h6>
          <form>
            <div class="form-group">
              <input class="form-control" type="url" placeholder="URI" v-model="settings.alex.uri">
            </div>
            <div class="form-group">
              <input class="form-control" type="email" placeholder="Email" v-model="settings.alex.username">
            </div>
            <div class="form-group">
              <input
                  class="form-control"
                  type="password"
                  placeholder="Password"
                  v-model="settings.alex.password"
              >
            </div>
          </form>
        </div>
      </div>
      <hr>
      <div class="text-right">
        <button class="btn btn-primary" v-on:click="saveSettings()">Save</button>
      </div>
    </section>

    <section class="mt-5">
      <h6>Synchronization</h6>
      <p class="text-muted">
        Synchronize projects and tests
      </p>
      <hr>
      <button class="btn btn-outline-secondary" @click="sync()">
        <font-awesome-icon icon="sync"></font-awesome-icon>
        <span class="ml-1">Synchronize</span>
      </button>
    </section>

  </div>
</template>

<script>
  import {syncApi} from '../../services/apis/sync-api';
  import {settingsApi} from '../../services/apis/settings-api';
  import {settingsService} from '../../services/settings.service';

  export default {
    name: 'jzd-settings-view',
    data() {
      return {
        settings: null
      };
    },
    created() {
      settingsApi.get()
        .then(res => this.settings = res.data)
        .catch(console.error);
    },
    methods: {
      saveSettings() {
        settingsApi.update(this.settings)
          .then(() => {
            this.$toasted.success('The configuration has been updated.');
            settingsService.setSettings(JSON.parse(JSON.stringify(this.settings)));
          })
          .catch(() => {
            this.$toasted.error('The configuration could not be updated.');
          });
      },

      sync() {
        syncApi.sync()
          .then(() => {
            this.$toasted.success('Synchronization complete.');
          })
          .catch(() => {
            this.$toasted.error('Synchronization failed.');
          });
      }
    }
  };
</script>

<style scoped>

</style>
