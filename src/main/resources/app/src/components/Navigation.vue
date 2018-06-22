<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">
      <router-link :to="{name: 'projects'}" class="navbar-brand">
        <em>
          <strong style="font-weight: 500">ALEX for Jira</strong>
          <br>
          <small class="text-muted">Adapter</small>
        </em>
      </router-link>
      <div class="collapse navbar-collapse">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item">
            <router-link :to="{name: 'projects'}" class="nav-link" active-class="active" exact>Projects</router-link>
          </li>
          <li class="nav-item" v-if="projectMapping != null">
            <router-link
                :to="{name: 'tests', params: {projectId: projectMapping.jiraProjectId}}"
                class="nav-link"
                active-class="active"
                exact
            >
              Tests
            </router-link>
          </li>
          <li class="nav-item">
            <router-link :to="{name: 'settings'}" class="nav-link" active-class="active" exact>Settings</router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
  import {createNamespacedHelpers} from 'vuex';

  const {mapState} = createNamespacedHelpers('projectMappings');

  /**
   * Component for the navigation bar.
   */
  export default {
    name: 'afj-navigation',
    computed: {
      ...mapState({
        projectMapping: state => state.currentProjectMapping
      })
    }
  };
</script>

<style scoped lang="scss">
  .nav-link {
    .active {
      font-weight: 500;
    }
  }

  .navbar-brand {
    line-height: 1rem;
    font-size: 1rem;
  }
</style>
