<template>
  <div>
    <div class="list-group" v-if="events.length > 0">
      <div class="list-group-item" v-for="event in events">
        {{event}}
      </div>
    </div>
    <div class="alert alert-info" v-else>
      There are no project related events.
    </div>
  </div>
</template>

<script>
  export default {
    name: 'jzd-project-event-list',
    props: {
      events: {
        type: Array,
        default: () => []
      }
    }
  };
</script>

<style scoped>
</style>
