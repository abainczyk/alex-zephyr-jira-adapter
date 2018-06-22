<template>
  <div>
    <div v-for="(cycleList, versionId) in cycles">

      <div v-if="versionsMap[versionId] != null">
        <h6 class="mb-0">{{versionsMap[versionId].name}}</h6>
        <hr>
      </div>

      <div class="list-group mb-3" v-for="cycleMap in cycleList">
        <div
            class="list-group-item list-group-item-action"
            v-for="(cycle, cycleId) in cycleMap"
            v-if="cycle.name != null && cycleId > -1"
            :key="cycleId"
        >
          <afj-cycle-list-item :cycle="cycle" :cycle-id="cycleId"></afj-cycle-list-item>
        </div>
      </div>

    </div>
  </div>
</template>

<script>

  /**
   * Display a list of versions and cycles in a version.
   */
  export default {
    name: 'afj-cycle-list',
    props: {

      /** The map of cycles (versionID -> cycle[]). */
      cycles: [Array, Object],

      /** The map (versionID -> version). */
      versionsMap: Object
    }
  };
</script>

<style scoped>
</style>
