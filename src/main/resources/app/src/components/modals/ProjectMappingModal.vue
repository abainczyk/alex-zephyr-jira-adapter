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
        <a
            href="#"
            class="list-group-item list-group-item-action"
            v-for="project in projects"
            :class="{'active': project === selectedProject}"
            @click.prevent="selectProject(project)"
        >
          <div>
            <strong>{{project.name}}</strong>
          </div>
          <small>{{project.baseUrl}}</small>
        </a>
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
  import {alexProjectApi} from '../../apis/alex/alex-project-api';

  export default {
    name: 'afj-project-mapping-modal',
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
          .then(res => this.projects = res.data)
          .catch(console.error);
      },

      selectProject(project) {
        this.selectedProject = project;
      },

      close() {
        if (this.selectedProject == null) {
          return;
        }

        const projectMapping = {
          alexProjectId: this.selectedProject.id,
          jiraProjectId: this.project.id
        };

        this.$store.dispatch('projectMappings/create', projectMapping)
          .then(() => {
            this.$toasted.success(`The projects ${this.project.name} and ${this.selectedProject.name} have been connected.`);
            this.$emit('close', projectMapping);
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
