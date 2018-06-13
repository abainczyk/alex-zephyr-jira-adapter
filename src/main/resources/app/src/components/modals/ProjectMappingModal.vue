<template>
  <div>
    <b-modal ref="modal" lazy>
      <template slot="modal-header">
        <h5>Select a project in ALEX</h5>
      </template>

      <p v-if="project != null">
        The project <strong>{{project.name}}</strong> is not connected to a project in ALEX yet.
        Please select a project you want to connect it to.
      </p>

      <div class="list-group" v-if="projects.length > 0">
        <div class="list-group-item" v-for="project in projects" v-bind:class="{'active': project === selectedProject}"
             @click="selectProject(project)">
          <div>
            <strong>{{project.name}}</strong>
          </div>
          <small>{{project.baseUrl}}</small>
        </div>
      </div>
      <div class="alert alert-info mb-0" v-else>
        There are no projects in ALEX.
      </div>

      <template slot="modal-footer">
        <button class="btn btn-primary" @click="close" v-bind:disabled="selectedProject == null">Ok</button>
        <button class="btn btn-outline-secondary" @click="dismiss">Cancel</button>
      </template>
    </b-modal>
  </div>
</template>

<script>
  import {alexProjectApi} from '../../services/apis/alex/alex-project-api';
  import {projectMappingApi} from '../../services/apis/project-mapping-api';

  export default {
    name: 'jzd-project-mapping-modal',
    data() {
      return {
        project: null,
        projects: [],
        selectedProject: null
      };
    },
    methods: {
      open(project) {
        this.project = project;
        this.$refs.modal.show();
        alexProjectApi.find()
          .then(res => {
            this.projects = res.data;
          })
          .catch(console.error);
      },

      selectProject(project) {
        this.selectedProject = project;
      },

      close() {
        if (this.selectedProject == null) {
          return;
        }

        const mapping = {
          alexProjectId: this.selectedProject.id,
          jiraProjectId: parseInt(this.project.id),
          jiraProjectKey: this.project.key
        };

        projectMappingApi.create(this.project.id, mapping)
          .then(() => {
            this.$toasted.success(`The projects ${this.project.name} and ${this.selectedProject.name} have been connected.`);
            this.$emit('close', mapping);
            this.$refs.modal.hide();
          })
          .catch(console.error);
      },

      dismiss() {
        this.$emit('dismiss');
        this.$refs.modal.hide();
      }
    }
  };
</script>

<style scoped>
  .list-group {
    max-height: 321px;
    overflow: auto;
  }
</style>
