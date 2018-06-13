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

        <b-tabs>
          <b-tab title="Tests" active>
            <br>

            <jzd-issue-event-list :events="messages.issues"></jzd-issue-event-list>
          </b-tab>
          <b-tab title="Project">
            <br>

            <jzd-project-event-list :events="messages.events"></jzd-project-event-list>
          </b-tab>
        </b-tabs>

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
  import {projectEventApi} from '../../services/apis/project-event-api';

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
          issues: [],
          projects: []
        }
      };
    },
    created() {
      const id = this.$route.params.projectId;

      jiraProjectApi.findOne(id)
        .then((response) => {
          console.log('jira:project', response);
          this.jira.project = response.data;
        })
        .catch(console.error);

      alexProjectApi.find()
        .then((response) => {
          console.log('alex:projects', response);
          const projects = response.data;

          projectMappingApi.findOne(id)
            .then((response) => {
              console.log('mapping', response);

              const mapping = response.data;
              this.alex.project = projects.find((p) => p.id === mapping.alexProjectId);
            })
            .catch(console.error);
        })
        .catch(console.error);

      issueEventApi.find(id)
        .then((res) => {
          this.messages.issues = res.data;
        })
        .catch(console.error);

      projectEventApi.find()
        .then((res) => {
          this.messages.projects = res.data;
        })
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
      }
    }
  };
</script>

<style scoped>

</style>
