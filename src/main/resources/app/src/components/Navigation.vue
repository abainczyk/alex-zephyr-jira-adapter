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
        <ul class="navbar-nav">
          <li class="nav-item">
            <a
                href="#"
                class="nav-link"
                @click.prevent="synchronize()"
                v-b-tooltip.hover
                title="Synchronize"
                placement="left"
            >
              <font-awesome-icon icon="sync" :spin="synchronizing"></font-awesome-icon>
            </a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
  import {projectService} from '../services/project.service';
  import {syncApi} from '../services/apis/sync-api';

  export default {
    name: 'jzd-navigation',
    data() {
      return {
        subscriptions: [],
        projectMapping: null,
        synchronizing: false
      };
    },
    created() {
      const sub = projectService.currentProjectMapping$.subscribe(mapping => this.projectMapping = mapping);
      this.subscriptions.push(sub);
    },
    destroyed() {
      this.subscriptions.forEach(sub => sub.unsubscribe());
    },
    methods: {
      synchronize() {
        this.synchronizing = true;
        syncApi.sync()
          .then(() => {
            this.$toasted.success('Synchronization complete. Please reload the window.');
            this.synchronizing = false;
          })
          .catch(() => {
            this.$toasted.success('Synchronization failed.');
            this.synchronizing = false;
          });
      }
    }
  };
</script>

<style lang="scss" scoped>
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
