<template>
  <div class="pl-1 py-1">
    <div class="cursor-pointer" @click="toggleFolder()">
      <div class="w-100 d-flex flex-row">
        <div class="w-100 d-flex flex-row align-items-center">
          <div class="w-100">
            <font-awesome-icon :icon="collapse ? 'folder' : 'folder-open'" fixed-width></font-awesome-icon>
            <span class="ml-1">{{folder.folderName}}</span>
          </div>
          <div>
            <span style="background: #f2f2f2; width: 140px; display: block; height: 10px" class="rounded">
              <span style="background: #28a745; display: block; height: 10px;" class="rounded"
                    :style="{'width': calculateFolderProgress(folder) + '%'}"></span>
            </span>
          </div>
        </div>
        <div class="flex-shrink-0 pl-2">
          <b-dropdown variant="outline-secondary border-0 rounded" size="sm" text="" :right="true" no-caret>
            <template slot="button-content">
              <font-awesome-icon icon="bars"></font-awesome-icon>
            </template>
            <b-dropdown-item @click="executeFolder()">
              Execute folder
            </b-dropdown-item>
          </b-dropdown>
        </div>
      </div>
    </div>
    <div class="ml-2 border-left pl-2" v-if="!collapse">
      <div
          class="p-1 d-flex flex-row cursor-pointer align-items-center"
          v-for="execution in executions"
          :key="execution.id"
      >
        <div class="w-100">
          <font-awesome-icon icon="file" fixed-width></font-awesome-icon>
          <em class="ml-1">{{execution.summary}}</em>
        </div>
        <div>
          <small
              class="rounded py-1 px-2"
              :style="{'background': status.status[execution.executionStatus].color}"
              style="color: #fff; margin-right: 2.2rem"
          >
            {{status.status[execution.executionStatus].name}}
          </small>
        </div>
      </div>
    </div>

    <jzd-folder-execute-modal ref="folderExecuteModal"></jzd-folder-execute-modal>
  </div>
</template>

<script>
  import {jiraCyclesApi} from '../../../apis/jira/jira-cycles-api';

  export default {
    name: 'jzd-folder-list-item',
    props: {
      folder: {
        type: Object,
        default: null
      }
    },
    data() {
      return {
        collapse: true,
        status: null
      };
    },
    computed: {
      executions() {
        return this.status == null ? [] : this.status.executions;
      }
    },
    methods: {
      toggleFolder() {
        this.collapse = !this.collapse;

        if (!this.collapse) {
          jiraCyclesApi.getTests(this.folder.cycleId, this.folder.folderId, this.folder.projectId, this.folder.versionId)
            .then(res => {
              this.status = res.data;
            })
            .catch(console.error);
        }
      },

      executeFolder() {
        this.$refs.folderExecuteModal.open(this.folder);
      },

      calculateFolderProgress(folder) {
        return (100 / folder.totalExecutions) * folder.totalExecuted;
      }
    }
  };
</script>

<style scoped>
</style>
