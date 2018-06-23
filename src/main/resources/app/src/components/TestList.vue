<template>
  <div>
    <div class="list-group" v-if="tests.length > 0">
      <div
          href="#"
          class="list-group-item"
          v-for="test in sortedTests"
      >
        <div class="d-flex flex-row align-items-center">
          <div class="w-100">
            {{test.fields.summary}}
            <span
                class="badge badge-info"
                v-if="testMappingsMap[test.id] != null && testMappingsMap[test.id].updates > 0"
            >
              {{testMappingsMap[test.id].updates}} Updates
            </span>
          </div>
          <div class="px-2">
            <span class="float-right badge badge-warning" v-if="testMappingsMap[test.id] == null">Not mapped</span>
            <span class="float-right badge badge-success" v-else>Mapped</span>
          </div>
          <div>
            <b-dropdown variant="outline-secondary border-0 rounded" size="sm" text="" :right="true" no-caret>
              <template slot="button-content">
                <div style="position: relative">
                  <font-awesome-icon icon="bars"></font-awesome-icon>
                </div>
              </template>
              <div v-if="testMappingsMap[test.id] == null">
                <b-dropdown-item @click="createMapping(test)">
                  Create mapping
                </b-dropdown-item>
              </div>
              <div v-else>
                <b-dropdown-item @click="executeTest(test)">
                  Execute test
                </b-dropdown-item>
                <b-dropdown-divider></b-dropdown-divider>
                <b-dropdown-item @click="updateTest(test)">
                  Update
                  <span
                      class="badge badge-info"
                      v-if="testMappingsMap[test.id] != null && testMappingsMap[test.id].updates > 0"
                  >
                    {{testMappingsMap[test.id].updates}}
                  </span>
                </b-dropdown-item>
                <b-dropdown-item @click="removeMapping(test)">
                  Remove mapping
                </b-dropdown-item>
              </div>
            </b-dropdown>
          </div>
        </div>
      </div>
    </div>
    <div class="alert alert-info" v-else>
      There are no test in this project.
    </div>

    <afj-test-mapping-modal ref="testMappingModal"></afj-test-mapping-modal>

    <afj-test-execute-modal ref="testExecuteModal"></afj-test-execute-modal>
  </div>
</template>

<script>
  import {jiraTestApi} from '../apis/jira/jira-test-api';

  export default {
    name: 'afj-test-list',
    props: {
      tests: {
        default: () => []
      },
      testMappingsMap: {
        default: () => ({})
      }
    },
    computed: {
      sortedTests() {
        return this.tests.sort((a, b) => {
          if (a.fields.summary < b.fields.summary) return -1;
          if (a.fields.summary > b.fields.summary) return 1;
          return 0;
        });
      },
      projectMapping() {
        return this.$store.state.projectMappings.currentProjectMapping;
      }
    },
    methods: {
      executeTest(test) {
        this.$refs.testExecuteModal.open(this.testMappingsMap[test.id]);
      },

      updateTest(test) {
        jiraTestApi.update(this.projectMapping.jiraProjectId, test.id)
          .then(() => {
            this.$toasted.success('The test has been updated in Jira');
            this.testMappingsMap[test.id].updates = 0;
          })
          .catch(err => {
            this.$toasted.error(`The test could not be updated in Jira. ${err.data.message}`);
          });
      },

      createMapping(test) {
        const testMapping = {
          jiraProjectId: test.fields.project.id,
          jiraTestId: test.id,
          alexProjectId: this.projectMapping.alexProjectId,
          alexTestId: null
        };

        this.$refs.testMappingModal.open(testMapping);
      },

      removeMapping(test) {
        this.$store.dispatch('testMappings/remove', this.testMappingsMap[test.id])
          .then(() => this.$toasted.success('The mapping has been removed for this test.'))
          .catch(console.error);
      }
    }
  };
</script>

<style scoped lang="scss">
</style>
