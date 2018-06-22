<template>
  <div>
    <h5>
      Projects
    </h5>
    <p class="text-muted">
      Choose a project to work with
    </p>
    <hr>
    <div class="list-group">
      <a
          href="#"
          class="list-group-item list-group-item-action d-flex flex-row"
          v-for="project in projects"
          @click.prevent="openProject(project)"
      >
        <div class="w-100">
          {{project.name}}
        </div>
        <div v-if="projectMappingsMap[project.id] != null">
          <span class="badge badge-success">Mapped</span>
        </div>
      </a>
    </div>

    <afj-project-mapping-modal ref="modal" v-on:close="onModalSuccess"></afj-project-mapping-modal>
  </div>
</template>

<script>

  /**
   * Component for the view that displays all projects in Jira and their mapping to a project in ALEX.
   */
  export default {
    name: 'afj-projects-view',
    created() {
      this.$store.dispatch('jira/projects/load')
        .catch(console.error);

      this.$store.dispatch('projectMappings/load')
        .catch(console.error);

      this.$store.commit('projectMappings/setCurrent', null);
    },
    computed: {

      /** All projects in Jira. */
      projects() {
        return this.$store.state.jira.projects.projects;
      },

      /** The map (jiraProjectId -> projectMapping). Used for look ups. */
      projectMappingsMap() {
        const map = {};
        this.$store.state.projectMappings.projectMappings.forEach(pm => map[pm.jiraProjectId] = pm);
        return map;
      }
    },
    methods: {

      /**
       * Opens a Jira project.
       *
       * @param {Object} project
       *    The project to work with.
       */
      openProject(project) {
        if (this.projectMappingsMap[project.id] == null) {
          this.$refs.modal.open(project);
        } else {
          this.$store.commit('projectMappings/setCurrent', this.projectMappingsMap[project.id]);
          this.$router.push({name: 'project', params: {projectId: project.id}});
        }
      },

      /**
       * Is called when the mapping has been created in the modal dialog.
       *
       * @param {Object} mapping
       *    The created mapping.
       */
      onModalSuccess(mapping) {
        this.$store.commit('projectMappings/setCurrent', mapping);
        this.$router.push({name: 'project', params: {projectId: mapping.jiraProjectId}});
      }
    }
  };
</script>

<style scoped>
</style>
