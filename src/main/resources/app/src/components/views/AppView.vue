<template>
  <div>
    <afj-status></afj-status>
    <afj-navigation></afj-navigation>
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
    name: 'afj-app-view'
  };
</script>

<style scoped>
</style>
