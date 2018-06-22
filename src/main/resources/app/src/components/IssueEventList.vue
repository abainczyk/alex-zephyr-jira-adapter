<template>
  <div>
    <div class="list-group" v-if="events.length > 0">
      <div class="list-group-item" v-for="event in events">
        <div class="text-muted text-right border-bottom pb-2 mb-2">
          {{formatDate(event.timestamp)}} | {{formatDateFromNow(event.timestamp)}}
        </div>
        <div class="d-flex flex-row">
          <div class="w-100">
            <div v-if="event.type === 'ISSUE_CREATED'">
              <strong>{{event.issueSummary}}</strong> has been created.
            </div>
            <div v-else-if="event.type === 'ISSUE_UPDATED'">
              <strong>{{event.issueSummary}}</strong> has been updated.
            </div>
            <div v-else-if="event.type === 'ISSUE_DELETED'">
              <strong>{{event.issueSummary}}</strong> has been deleted.
            </div>
          </div>
          <div>
            <button class="btn btn-sm btn-light" @click="deleteMessage(event)">Ok</button>
          </div>
        </div>
      </div>
    </div>
    <div class="alert alert-info" v-else>
      There are no new messages.
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
      },

      deleteMessage(message) {
        this.$store.dispatch('messages/remove', message)
          .catch(console.error);
      }
    }
  };
</script>

<style scoped>
</style>
