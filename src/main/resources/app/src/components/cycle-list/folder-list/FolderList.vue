<template>
  <div>
    <afj-folder-list-item
        v-for="folder in folders"
        :folder="folder"
        :key="folder.id"
    ></afj-folder-list-item>
  </div>
</template>

<script>

  /**
   * Displays a list of folders in a test cycle.
   */
  export default {
    name: 'afj-folder-list',
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
