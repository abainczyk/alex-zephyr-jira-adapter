<template>
  <div>
    <jzd-status></jzd-status>
    <jzd-navigation></jzd-navigation>
    <div class="container mt-3">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
  import {setupApi} from '../../services/apis/setup-api';

  export default {
    name: 'jzd-app-view',
    created() {
      setupApi.get()
        .then(res => {
          const data = res.data;
          if (data.jira == null && data.alex == null) {
            this.$router.push({name: 'setup'});
            return;
          }

          if (this.$route.name === 'app') {
            this.$router.push({name: 'projects'});
          }
        })
        .catch(console.error);
    }
  };
</script>

<style scoped>

</style>
