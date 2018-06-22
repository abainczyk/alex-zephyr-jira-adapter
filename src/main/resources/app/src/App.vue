<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>

  /**
   * The component that bootstraps the app.
   */
  export default {
    name: 'afj-app'
  };
</script>

<style lang="scss">
  @import "../node_modules/bootstrap/scss/bootstrap";

  /*
   * Make the font size a little smaller than the default bootstrap font size.
   * .875rem should be 14px.
   */
  html {
    font-size: .875rem;
  }

  /* Positioning and styling for the toasts of the 'toastr' plugin. */
  .toasted-container {
    &.bottom-center {
      bottom: 3rem;
    }

    .toasted {
      padding: 1.25rem 1.75rem;
      border-radius: .25rem;
      max-width: 360px;
    }
  }

  .cursor-pointer {
    cursor: pointer;
  }

  .cursor-grabbing {
    cursor: grabbing;
  }

  /* ----------------------------------------------
   * Extensions to bootstrap.
   * ---------------------------------------------- */

  /* Bring back the really small button from Bootstrap 3. */
  .btn {
    &.btn-xs {
      padding: 0 .25rem;
    }
  }

  /* Outline badges. */
  .badge {
    &.badge-outline-success {
      background: none;
      border: 1px solid $green;
      color: $green;
    }

    &.badge-outline-danger {
      background: none;
      border: 1px solid $danger;
      color: $danger;
    }

    &.badge-outline-secondary {
      background: none;
      border: 1px solid $secondary;
      color: $secondary;
    }
  }
</style>
