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

  /**
   * The entry point component for the router.
   */
  export default {
    name: 'jzd-app-view'
  };
</script>

<style scoped>
</style>
