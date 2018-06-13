<template>
  <div class="container mt-3">
    <h4>Setup</h4>

    <hr>

    <div class="btn-group d-flex">
      <button class="btn text-left w-100" v-bind:class="getButtonClass(0)" v-on:click="setStep(0)">
        <small>Step 1</small>
        <br>
        <strong>Connect with Jira</strong>
      </button>
      <button class="btn text-left w-100" v-bind:class="getButtonClass(1)" v-on:click="setStep(1)">
        <small>Step 2</small>
        <br>
        <strong>Connect with ALEX</strong>
      </button>
    </div>

    <hr>

    <div v-if="step === 0">
      <jzd-connect-with-jira-step
          v-on:next="nextStep"
          v-bind:data="steps[0]"
      >
      </jzd-connect-with-jira-step>
    </div>
    <div v-else-if="step === 1">
      <jzd-connect-with-alex-step
          v-on:finish="finish"
          v-on:back="previousStep()"
          v-bind:data="steps[1]"
      >
      </jzd-connect-with-alex-step>
    </div>

  </div>
</template>

<script>
  import {setupApi} from '../../../services/apis/setup-api';

  export default {
    name: 'jzd-setup-view',
    data() {
      return {
        step: 0,
        steps: {
          0: {
            config: {
              appId: 'jira',
              uri: 'http://',
              username: null,
              password: null
            },
            valid: false,
            touched: false
          },
          1: {
            config: {
              appId: 'alex',
              uri: 'http://',
              username: null,
              password: null
            },
            valid: false,
            touched: false
          }
        }
      };
    },
    created() {
      setupApi.get()
        .then(response => {
          const data = response.data;
          if (data.jira === true && data.alex === true) {
            this.$router.push('/');
          }
        })
        .catch(console.error);
    },
    methods: {
      getButtonClass(step) {
        return step === this.step ? 'btn-primary' : 'btn-outline-primary';
      },

      setStep(step) {
        this.step = step;
      },

      nextStep(data) {
        this.steps[0] = data;
        this.step = 1;
      },

      previousStep() {
        this.step = 0;
      },

      finish(data) {
        this.steps[1] = data;
        const setupData = {
          jiraSettings: this.steps[0].config,
          alexSettings: this.steps[1].config
        };
        setupApi.setup(setupData)
          .then(() => {
            this.$toasted.success('The adapter has been setup successfully.');
            this.$router.push('app');
          })
          .catch(console.error);
      }
    }
  };
</script>

<style scoped>

</style>
