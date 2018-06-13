<template>
  <b-modal ref="modal" lazy>
    <template slot="modal-header">
      <h5>Execute tests in a folder</h5>
    </template>

    <div class="alert alert-danger" v-if="errorMessage != null" v-html="errorMessage"></div>

    <p>
      Select a target to execute the tests in the folder.
    </p>

    <jzd-project-url-list
        v-if="project != null"
        :urls="project.urls"
        :selected-url="selectedUrl"
        v-on:selected="selectUrl"
    >
    </jzd-project-url-list>

    <template slot="modal-footer">
      <button class="btn btn-primary" @click="execute()">Execute</button>
      <button class="btn btn-outline-secondary" @click="dismiss()">Cancel</button>
    </template>
  </b-modal>
</template>

<script>
  import {alexProjectApi} from '../../services/apis/alex/alex-project-api';
  import {projectMappingApi} from '../../services/apis/project-mapping-api';
  import {jiraCyclesApi} from '../../services/apis/jira/jira-cycles-api';

  export default {
    name: 'jzd-folder-execute-modal',
    data() {
      return {
        errorMessage: null,
        data: null,
        project: null,
        selectedUrl: null
      };
    },
    methods: {
      open(folder) {
        this.folder = folder;
        console.log(this.folder);

        projectMappingApi.findOne(this.folder.projectId)
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
          projectId: this.folder.projectId,
          cycleId: this.folder.cycleId,
          versionId: this.folder.versionId,
          folderId: this.folder.folderId,
          alexUrlId: this.selectedUrl.id
        };

        jiraCyclesApi.executeFolder(this.folder.cycleId, this.folder.folderId, config)
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
