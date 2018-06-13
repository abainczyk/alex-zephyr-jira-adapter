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
          class="list-group-item list-group-item-action"
          v-for="project in projects"
          @click.prevent="open(project)"
      >
        {{project.name}}
      </a>
    </div>

    <jzd-project-mapping-modal ref="configModal" v-on:close="handleModalClose"></jzd-project-mapping-modal>
  </div>
</template>

<script>
  import {jiraProjectApi} from '../../services/apis/jira/jira-project-api';
  import {projectMappingApi} from '../../services/apis/project-mapping-api';
  import {projectService} from '../../services/project.service';

  export default {
    name: 'jzd-projects-view',
    data() {
      return {
        projects: []
      };
    },
    created() {
      jiraProjectApi.find()
        .then(response => {
          this.projects = response.data;
        })
        .catch(console.error);
    },
    methods: {
      open(project) {
        projectMappingApi.findOne(project.id)
          .then(res => {
            if (res.data === '') {
              this.$refs.configModal.open(project);
            } else {
              projectService.setCurrentProjectMapping(res.data);
              this.$router.push({name: 'project', params: {projectId: project.id}});
            }
          })
          .catch(console.error);
      },
      handleModalClose(mapping) {
        projectService.setCurrentProjectMapping(mapping);
        this.$router.push({name: 'project', params: {projectId: mapping.jiraProjectId}});
      }
    }
  };
</script>

<style scoped>

</style>
