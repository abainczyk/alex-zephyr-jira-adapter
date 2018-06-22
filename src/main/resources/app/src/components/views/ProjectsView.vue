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
          @click.prevent="open(project)"
      >
        <div class="w-100">
          {{project.name}}
        </div>
        <div v-if="projectMappingsMap[project.id] != null">
          <span class="badge badge-success">Mapped</span>
        </div>
      </a>
    </div>

    <afj-project-mapping-modal ref="configModal" v-on:close="handleModalClose"></afj-project-mapping-modal>
  </div>
</template>

<script>
  import {projectMappingApi} from '../../apis/project-mapping-api';

  export default {
    name: 'afj-projects-view',
    created() {
      this.$store.dispatch('jira/projects/load')
        .catch(console.error);

      this.$store.dispatch('projectMappings/load')
        .catch(console.error);

      this.$store.commit('projectMappings/setCurrent', null)
    },
    computed: {
      projects() {
        return this.$store.state.jira.projects.projects;
      },
      projectMappingsMap() {
        const map = {};
        this.$store.state.projectMappings.projectMappings.forEach(pm => map[pm.jiraProjectId] = pm);
        return map;
      }
    },
    methods: {

      open(project) {
        projectMappingApi.findOne(project.id)
          .then(res => {
            if (res.data === '') {
              this.$refs.configModal.open(project);
            } else {
              this.$store.commit('projectMappings/setCurrent', res.data);
              this.$router.push({name: 'project', params: {projectId: project.id}});
            }
          })
          .catch(console.error);
      },

      handleModalClose(mapping) {
        this.$store.commit('projectMappings/setCurrent', mapping);
        this.$router.push({name: 'project', params: {projectId: mapping.jiraProjectId}});
      }
    }
  };
</script>

<style scoped>
</style>
