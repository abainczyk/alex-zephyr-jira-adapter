<template>
  <div>
    <div class="list-group" v-if="events.length > 0">
      <div class="list-group-item" v-for="event in events">
        <div class="d-flex flex-row border-bottom pb-2 mb-2">
          <div class="w-25">
            Test
            <span class="badge badge-success" v-if="event.type === 'ISSUE_CREATED'">created</span>
            <span class="badge badge-info" v-else-if="event.type === 'ISSUE_UPDATED'">updated</span>
            <span class="badge badge-danger" v-else-if="event.type === 'ISSUE_DELETED'">deleted</span>
          </div>
          <div class="w-75 text-muted text-right">
            {{formatDate(event.timestamp)}} | {{formatDateFromNow(event.timestamp)}}
          </div>
        </div>

        <div v-if="event.type === 'ISSUE_CREATED'">
          <router-link :to="{name: 'test', params: {projectId: event.projectId, testId: event.issueId}}">
            <strong>{{event.issueSummary}}</strong>
          </router-link>
          has been created.
        </div>
        <div v-else-if="event.type === 'ISSUE_UPDATED'">
          <router-link :to="{name: 'test', params: {projectId: event.projectId, testId: event.issueId}}">
            <strong>{{event.issueSummary}}</strong>
          </router-link>
          has been updated.
        </div>
        <div v-else-if="event.type === 'ISSUE_DELETED'">
          <router-link :to="{name: 'test', params: {projectId: event.projectId, testId: event.issueId}}">
            <strong>{{event.issueSummary}}</strong>
          </router-link>
          has been deleted.
        </div>
      </div>
    </div>
    <div class="alert alert-info" v-else>
      There are no test related events.
    </div>
  </div>
</template>

<script>
  import moment from 'moment';

  export default {
    name: 'jzd-issue-event-list',
    props: {
      events: {
        type: Array,
        default: () => []
      }
    },
    methods: {
      formatDate(timestamp) {
        return moment(timestamp).format('DD.mm.YYYY, LT');
      },

      formatDateFromNow(timestamp) {
        return moment(timestamp).fromNow();
      }
    }
  };
</script>

<style scoped>
</style>
