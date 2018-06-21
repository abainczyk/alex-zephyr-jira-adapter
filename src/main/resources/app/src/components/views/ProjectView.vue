<template>
  <div>
    <div class="row">
      <div class="col col-4">

        <router-link
            :to="{name: 'tests', params: {id: jira.project.id}}"
            class="btn btn-primary btn-block text-left mb-3"
            v-if="jira.project != null"
        >
          Edit tests
        </router-link>

        <div v-if="jira.project != null && alex.project != null">
          <div class="card">
            <div class="card-body">
              <p>
                <strong>{{jira.project.name}}</strong> is connected to <strong>{{alex.project.name}}</strong>.
              </p>
              <a href="#" class="btn btn-sm btn-outline-secondary" @click.prevent="openConfirmModal()">
                <font-awesome-icon icon="unlink"></font-awesome-icon>
                Remove connection
              </a>
            </div>
          </div>
        </div>

      </div>
      <div class="col col-8">

        <h5>Messages</h5>
        <hr>

        <jzd-issue-event-list :events="messages.issues" @deleted="onIssueDeleted"></jzd-issue-event-list>

      </div>
    </div>

    <jzd-confirm-modal ref="confirmModal" v-on:close="removeConnection()"></jzd-confirm-modal>
  </div>
</template>

<script>
  import {jiraProjectApi} from '../../services/apis/jira/jira-project-api';
  import {alexProjectApi} from '../../services/apis/alex/alex-project-api';
  import {projectMappingApi} from '../../services/apis/project-mapping-api';
  import {issueEventApi} from '../../services/apis/issue-event-api';

  export default {
    name: 'jzd-project-view',
    data() {
      return {
        mapping: null,
        jira: {
          project: null
        },
        alex: {
          project: null
        },
        messages: {
          issues: []
        }
      };
    },
    created() {
      const projectId = this.$route.params.projectId;

      jiraProjectApi.findOne(projectId)
        .then(res => this.jira.project = res.data)
        .catch(console.error);

      alexProjectApi.find()
        .then(res => {
          const projects = res.data;
          return projectMappingApi.findOne(projectId)
            .then((response) => {
              const mapping = response.data;
              this.alex.project = projects.find((p) => p.id === mapping.alexProjectId);
            });
        })
        .catch(console.error);

      issueEventApi.find(projectId)
        .then(res => this.messages.issues = res.data)
        .catch(console.error);
    },
    methods: {
      openConfirmModal() {
        this.$refs.confirmModal.open('Do you want to remove the connection? All related data will be deleted as well.');
      },

      removeConnection() {
        projectMappingApi.delete(this.jira.project.id)
          .then(() => {
            this.$router.push('/app/projects');
            this.$toasted.success(`The connection between ${this.jira.project.name} and ${this.alex.project.name} has been removed.`);
          })
          .catch(console.error);
      },

      onIssueDeleted(event) {
        const i = this.messages.issues.findIndex(e => e.id === event.id);
        if (i > -1) this.messages.issues.splice(i, 1);
      }
    }
  };
</script>

<style scoped>

</style>
