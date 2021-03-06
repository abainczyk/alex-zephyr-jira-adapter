<template>
  <b-modal ref="modal" lazy>
    <template slot="modal-header">
      <h5>Execute a test cycle</h5>
    </template>

    <div class="alert alert-danger" v-if="errorMessage != null" v-html="errorMessage"></div>

    <p>
      Select a target to execute the tests of the cycle.
    </p>

    <afj-project-url-list
        v-if="project != null"
        :urls="project.urls"
        :selected-url="selectedUrl"
        v-on:selected="selectUrl"
    >
    </afj-project-url-list>

    <template slot="modal-footer">
      <button class="btn btn-primary" @click="execute()">Execute</button>
      <button class="btn btn-outline-secondary" @click="dismiss()">Cancel</button>
    </template>
  </b-modal>
</template>

<script>
  import {alexProjectApi} from '../../apis/alex/alex-project-api';
  import {projectMappingApi} from '../../apis/project-mapping-api';
  import {jiraCyclesApi} from '../../apis/jira/jira-cycles-api';

  export default {
    name: 'afj-cycle-execute-modal',
    data() {
      return {
        errorMessage: null,
        data: null,
        project: null,
        selectedUrl: null
      };
    },
    methods: {
      open(data) {
        this.data = data;

        projectMappingApi.findOneByJiraProjectId(data.cycle.projectId)
          .then(res => {
            return alexProjectApi.findOne(res.data.alexProjectId)
              .then(res => {
                this.project = res.data;
                this.selectedUrl = this.project.urls.find(u => u.default);
              });
          })
          .catch(console.error);

        this.$refs.modal.show();
      },

      selectUrl(url) {
        this.selectedUrl = url;
      },

      execute() {
        this.errorMessage = null;

        const config = {
          projectId: this.data.cycle.projectId,
          cycleId: this.data.cycleId,
          versionId: this.data.cycle.versionId,
          alexUrlId: this.selectedUrl.id
        };

        jiraCyclesApi.executeCycle(this.data.cycleId, config)
          .then(() => this.close())
          .catch(console.error);
      },

      close() {
        this.$emit('close');
        this.$refs.modal.hide();
      },

      dismiss() {
        this.$emit('dismiss');
        this.$refs.modal.hide();
      }
    }
  };
</script>

<style scoped>
</style>
