<template>
  <div>
    <jzd-folder-list-item
        v-for="folder in folders"
        :folder="folder"
        :key="folder.id"
    ></jzd-folder-list-item>
  </div>
</template>

<script>

  /**
   * Displays a list of folders in a test cycle.
   */
  export default {
    name: 'jzd-folder-list',
    props: {

      /** The folders in a cycle. */
      folders: {
        type: Array,
        default: () => []
      }
    }
  };
</script>

<style scoped>
</style>
