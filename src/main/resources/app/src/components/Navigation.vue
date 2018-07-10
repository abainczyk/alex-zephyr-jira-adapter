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
      <div class="navbar-collapse">
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
          <li class="nav-item" v-if="currentUser.role === 'ADMIN'">
            <router-link :to="{name: 'adminUsers'}" class="nav-link" active-class="active" exact>Users</router-link>
          </li>
        </ul>
        <ul class="navbar-nav float-right">
          <li class="nav-item">
            <a class="nav-link" href="" @click.prevent="logout()">Logout</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>

  /**
   * Component for the navigation bar.
   */
  export default {
    name: 'afj-navigation',
    computed: {
      projectMapping() {
        return this.$store.state.projectMappings.currentProjectMapping;
      },
      currentUser() {
        return this.$store.state.users.currentUser;
      }
    },
    methods: {
      logout() {
        this.$store.dispatch('users/signOut')
          .then(() => {
            this.$toasted.success('You have signed out.');
            this.$router.push({name: 'login'});
          });
      }
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
