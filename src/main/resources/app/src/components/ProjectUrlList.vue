<template>
  <div class="list-group">
    <a
        href="#"
        class="list-group-item list-group-item-action"
        v-for="url in urls"
        @click.prevent="selectUrl(url)"
        :class="{'active': url.id === selectedUrl.id}"
    >
      <strong class="d-block" v-if="url.name">{{url.name}}</strong>
      {{url.url}}
    </a>
  </div>
</template>

<script>

  /**
   * Component that displays a selectable list of URLs that are registered in ALEX to the current project.
   */
  export default {
    name: 'afj-project-url-list',
    props: {

      /** The URLs in ALEX. */
      urls: {
        type: Array,
        default: () => []
      },

      /** The selected URL. */
      selectedUrl: {
        type: Object,
        default: () => ({})
      }
    },
    methods: {

      /**
       * Select a URL from the list.
       *
       * @param {Object} url
       *    The URL to select.
       */
      selectUrl(url) {
        this.$emit('selected', url);
      }
    }
  };
</script>

<style scoped>
</style>
