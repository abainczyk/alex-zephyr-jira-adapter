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
  export default {
    name: 'jzd-project-url-list',
    props: {
      urls: {
        default: () => []
      },
      selectedUrl: {
        default: () => ({})
      }
    },
    methods: {
      selectUrl(url) {
        this.$emit('selected', url);
      }
    }
  };
</script>

<style scoped>
</style>
