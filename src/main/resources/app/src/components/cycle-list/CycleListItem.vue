<template>
  <div>
    <div class="d-flex flex-row">
      <div class="w-100 cursor-pointer d-flex align-items-center" @click="toggleCycle()">
        <div class="w-100">
          <font-awesome-icon :icon="collapse ? 'folder' : 'folder-open'" fixed-width></font-awesome-icon>
          <span class="ml-1">{{cycle.name}}</span>
        </div>
        <div>
          <span style="background: #f2f2f2; width: 140px; display: block; height: 10px" class="rounded">
            <span style="background: #28a745; display: block; height: 10px;" class="rounded"
                  :style="{'width': calculateCycleProgress(cycle) + '%'}"></span>
          </span>
        </div>
      </div>
      <div class="pl-2">
        <b-dropdown variant="outline-secondary border-0 rounded" size="sm" text="" :right="true" no-caret>
          <template slot="button-content">
            <font-awesome-icon icon="bars"></font-awesome-icon>
          </template>
          <b-dropdown-item @click="openCycleExecuteModal()">
            Execute cycle
          </b-dropdown-item>
        </b-dropdown>
      </div>
    </div>

    <div v-if="!collapse" class="pl-2 border-left ml-2">
      <jzd-folder-list :folders="folders"></jzd-folder-list>
    </div>

    <jzd-cycle-execute-modal ref="cycleExecuteModal"></jzd-cycle-execute-modal>
  </div>
</template>

<script>
  import {jiraCyclesApi} from '../../services/apis/jira/jira-cycles-api';
  import JzdCycleExecuteModal from '../modals/CycleExecuteModal';

  export default {
    name: 'jzd-cycle-list-item',
    components: {JzdCycleExecuteModal},
    props: {

      /** The cycle. */
      cycle: Object,

      /** The ID of the cycle. */
      cycleId: String
    },
    data() {
      return {
        folders: [],
        collapse: true
      };
    },
    methods: {
      toggleCycle() {
        this.collapse = !this.collapse;
        if (!this.collapse) {
          jiraCyclesApi.getFolders(this.cycleId, this.cycle.projectId, this.cycle.versionId)
            .then(res => this.folders = res.data)
            .catch(console.error);
        }
      },

      openCycleExecuteModal() {
        this.$refs.cycleExecuteModal.open({
          cycle: this.cycle,
          cycleId: this.cycleId
        });
      },

      /**
       * Calculate how many tests have been executed in the cycle in percent.
       *
       * @param {Object} cycle
       *    The cycle.
       * @return {number}
       *    The number of executed tests in the cycle in percent.
       */
      calculateCycleProgress(cycle) {
        return (100 / cycle.totalExecutions) * cycle.totalExecuted;
      }
    }
  };
</script>

<style scoped>

</style>
