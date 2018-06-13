<template>
  <div>
    <form>
      <div class="form-group">
        <label>ALEX URL</label>
        <input class="form-control" type="text" v-model="data.config.uri">
        <small class="form-text text-muted">Specify the base URL where ALEX is accessed</small>
      </div>
      <div class="form-group">
        <label>Email</label>
        <input class="form-control" type="text" v-model="data.config.username">
        <small class="form-text text-muted">You can use any valid user</small>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input class="form-control" type="password" v-model="data.config.password">
      </div>
    </form>

    <hr>

    <div class="text-right">
      <button class="btn btn-outline-secondary mr-2" v-on:click="$emit('back')">
        <font-awesome-icon icon="angle-left"></font-awesome-icon>
        Back
      </button>
      <button class="btn btn-primary" v-on:click="finish()">
        Finish
        <font-awesome-icon icon="check"></font-awesome-icon>
      </button>
    </div>
  </div>
</template>

<script>
  import {setupApi} from '../../../services/apis/setup-api';

  export default {
    name: 'jzd-connect-with-alex-step',
    props: ['data'],
    methods: {
      finish() {
        setupApi.step2(this.data.config)
          .then(() => {
            this.data.valid = true;
            this.$emit('finish', this.data);
          })
          .catch(err => {
            this.$toasted.error(`Could not connect to ALEX. ${err.message}`);
          });
      }
    }
  };
</script>

<style scoped>

</style>
