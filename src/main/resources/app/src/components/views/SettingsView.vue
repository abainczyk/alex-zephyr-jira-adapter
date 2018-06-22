<template>
  <div class="mt-3">
    <h5>Settings</h5>
    <hr>

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
  import {syncApi} from '../../apis/sync-api';

  /**
   * Settings view.
   */
  export default {
    name: 'afj-settings-view',
    methods: {

      /** Sync the tests and projects between ALEX and Jira. */
      sync() {
        syncApi.sync()
          .then(() => this.$toasted.success('Synchronization complete.'))
          .catch(() => this.$toasted.error('Synchronization failed.'));
      }
    }
  };
</script>

<style scoped>
</style>
