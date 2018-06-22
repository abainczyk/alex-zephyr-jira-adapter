<template>
  <div>
    <div class="row">
      <div class="col-sm-12 col-md-4">

        <router-link
            :to="{name: 'tests', params: {id: jiraProject.id}}"
            class="btn btn-primary btn-block text-left mb-3"
            v-if="jiraProject != null"
        >
          Edit tests
        </router-link>

        <div v-if="jiraProject != null && alexProject != null">
          <div class="card">
            <div class="card-body">
              <p>
                <strong>{{jiraProject.name}}</strong> is connected to <strong>{{alexProject.name}}</strong>.
              </p>
              <a href="#" class="btn btn-sm btn-outline-secondary" @click.prevent="openConfirmModal()">
                <font-awesome-icon icon="unlink"></font-awesome-icon>
                Remove connection
              </a>
            </div>
          </div>
        </div>

      </div>
      <div class="col-sm-12 col-md-8 mt-sm-4 mt-md-0">

        <h5>Messages</h5>
        <hr>

        <afj-issue-event-list :events="messages"></afj-issue-event-list>

      </div>
    </div>

    <afj-confirm-modal ref="confirmModal" v-on:close="onSuccessFromConfirmModal()"></afj-confirm-modal>
  </div>
</template>

<script>
  import {jiraProjectApi} from '../../apis/jira/jira-project-api';
  import {alexProjectApi} from '../../apis/alex/alex-project-api';

  export default {
    name: 'afj-project-view',
    data() {
      return {
        jiraProject: null,
        alexProject: null
      };
    },
    computed: {
      messages() {
        return this.$store.state.messages.messages;
      },
      projectMapping() {
        return this.$store.state.projectMappings.currentProjectMapping;
      }
    },
    created() {
      jiraProjectApi.findOne(this.projectMapping.jiraProjectId)
        .then(res => this.jiraProject = res.data)
        .catch(console.error);

      alexProjectApi.findOne(this.projectMapping.alexProjectId)
        .then(res => this.alexProject = res.data)
        .catch(console.error);

      this.$store.dispatch('messages/load', this.projectMapping.jiraProjectId)
        .catch(console.error);
    },
    methods: {

      openConfirmModal() {
        this.$refs.confirmModal.open('Do you want to remove the connection? All related data will be deleted as well.');
      },

      onSuccessFromConfirmModal() {
        this.$store.dispatch('projectMappings/remove', this.jiraProject.id)
          .then(() => {
            this.$router.push('/app/projects');
            this.$toasted.success(`The connection between ${this.jiraProject.name} and ${this.alexProject.name} has been removed.`);
          })
          .catch(console.error);
      }
    }
  };
</script>

<style scoped>

</style>
