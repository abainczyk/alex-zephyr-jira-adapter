<template>
  <b-modal ref="modal" lazy :size="size">
    <template slot="modal-header">
      <h5>Create test mapping</h5>
    </template>

    <div class="alert alert-danger" v-if="errorMessage != null" v-html="errorMessage"></div>

    <div v-if="rootTestSuite == null">
      <p>
        There is no test mapping defined for this test yet.
        Do you want to create a new test in ALEX or use an existing one?
      </p>

      <button class="btn btn-outline-secondary btn-block" @click.prevent="createTestMapping()">
        Create new test
      </button>

      <button class="btn btn-outline-secondary btn-block" @click.prevent="useExistingTest()">
        Use existing test
      </button>
    </div>
    <div v-else>
      <p>
        <strong>Select an existing test in ALEX</strong>
      </p>
      <hr>

      <afj-test-tree
          :test-suite="rootTestSuite"
          :test-mappings-map="testMappingsMap"
          @selected="selectTestCase"
      >
      </afj-test-tree>
    </div>

    <template slot="modal-footer">
      <button class="btn btn-outline-secondary" @click="dismiss()">Cancel</button>
    </template>
  </b-modal>
</template>

<script>
  import {testMappingApi} from '../../apis/test-mapping-api';
  import {alexTestsApi} from '../../apis/alex/alex-tests-api';

  export default {
    name: 'afj-test-mapping-setup-modal',
    data() {
      return {
        errorMessage: null,
        testMapping: null,
        testMappingsMap: {},
        rootTestSuite: null,
        size: 'md'
      };
    },
    methods: {
      open(testMapping) {
        this.size = 'md';
        this.testMapping = testMapping;
        this.rootTestSuite = null;
        this.$refs.modal.show();
      },

      createTestMapping() {
        this.errorMessage = null;

        this.$store.dispatch('testMappings/create', this.testMapping)
          .then(res => {
            this.$toasted.success('The test has been created in ALEX.');
            this.close(res.data);
          })
          .catch(err => this.errorMessage = err.data.message);
      },

      useExistingTest() {
        const projectId = this.testMapping.jiraProjectId;

        alexTestsApi.findRoot(this.testMapping.alexProjectId)
          .then(res1 => {
            this.size = 'lg';

            return testMappingApi.find(projectId)
              .then(res => {
                res.data.forEach(mapping => {
                  this.testMappingsMap[mapping.alexTestId] = mapping;
                });
                this.rootTestSuite = res1.data;
              });
          })
          .catch(console.error);
      },

      selectTestCase(testCase) {
        const mapping = Object.assign({}, this.testMapping);
        mapping.alexTestId = testCase.id;

        this.$store.dispatch('testMappings/create', mapping)
          .then(res => {
            this.$toasted.success('The test has been created in ALEX.');
            this.close(res.data);
          })
          .catch(err => this.errorMessage = err.data.message);
      },

      close(mapping) {
        this._reset();
        this.$refs.modal.hide();
        this.$emit('close', mapping);
      },

      dismiss() {
        this._reset();
        this.$refs.modal.hide();
        this.$emit('dismiss');
      },

      _reset() {
        this.testMappingsMap = {};
        this.testMapping = null;
        this.errorMessage = null;
        this.size = 'md';
      }
    }
  };
</script>

<style scoped>
</style>
