<template>
  <b-modal ref="modal" lazy>
    <template slot="modal-header">
      <h5>Execute a test</h5>
    </template>

    <div class="alert alert-danger" v-if="errorMessage != null" v-html="errorMessage"></div>

    <p>
      Select a target to execute the test.
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
  import {jiraTestApi} from '../../apis/jira/jira-test-api';
  import {projectMappingApi} from '../../apis/project-mapping-api';

  export default {
    name: 'afj-test-execute-modal',
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

        projectMappingApi.findOneByJiraProjectId(data.jiraProjectId)
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

        this.data.alexUrlId = this.selectedUrl.id;
        jiraTestApi.execute(this.data.jiraProjectId, this.data.jiraTestId, this.data)
          .then(() => {
            this.$toasted.success('The test process has been started.');
            this.dismiss();
          })
          .catch(err => {
            this.errorMessage = err.message;
          });
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
