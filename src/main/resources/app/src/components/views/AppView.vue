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
  export default {
    name: 'jzd-app-view',
    created() {
      this.$router.push({name: 'projects'});
    }
  };
</script>

<style scoped>

</style>
