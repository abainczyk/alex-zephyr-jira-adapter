<template>
  <div>
    <form>
      <div class="form-group">
        <label>Jira URL</label>
        <input class="form-control" type="text" v-model="data.config.uri">
        <small class="form-text text-muted">Specify the base URL where Jira is accessed</small>
      </div>
      <div class="form-group">
        <label>User</label>
        <input class="form-control" type="text" v-model="data.config.username">
        <small class="form-text text-muted">You have to use a valid account with admin rights</small>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input class="form-control" type="password" v-model="data.config.password">
      </div>
    </form>

    <hr>

    <div class="text-right">
      <button class="btn btn-primary" v-on:click="next()">
        Next
        <font-awesome-icon icon="angle-right"></font-awesome-icon>
      </button>
    </div>
  </div>
</template>

<script>
  import {setupApi} from '../../../services/apis/setup-api';

  export default {
    name: 'jzd-connect-with-jira-step',
    props: ['data'],
    methods: {
      next() {
        setupApi.step1(this.data.config)
          .then(() => {
            this.data.valid = true;
            this.$emit('next', this.data);
          })
          .catch(err => {
            this.$toasted.error(`Could not connect to Jira. ${err.message}`);
            console.error(err);
          });
      }
    }
  };
</script>

<style scoped>

</style>
