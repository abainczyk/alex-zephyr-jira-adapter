<template>
  <div>
    <h5>Tests</h5>
    <p class="text-muted">
      Choose a test to edit
    </p>
    <hr>

    <jzd-execution-status></jzd-execution-status>
    <hr>

    <b-tabs>
      <b-tab title="Tests" active>
        <div class="mt-3 text-right">

          <button class="btn btn-sm btn-light mr-2" @click="refreshTests()">
            <font-awesome-icon icon="sync" fixed-width class="mr-1" :spin="refreshingTests"></font-awesome-icon>
            Refresh
          </button>

          <b-dropdown variant="light rounded" size="sm" text="" :right="true" no-caret>
            <template slot="button-content">
              <font-awesome-icon icon="filter" fixed-width class="mr-1"></font-awesome-icon>
              Filter
            </template>
            <b-dropdown-item @click="activeTestFilter = testFilters.ALL">
              <font-awesome-icon
                  icon="check"
                  class="mr-1"
                  :class="{'invisible': activeTestFilter !== testFilters.ALL}"
              >
              </font-awesome-icon>
              All
            </b-dropdown-item>
            <b-dropdown-item @click="activeTestFilter = testFilters.UNMAPPED_ONLY">
              <font-awesome-icon
                  icon="check"
                  class="mr-1"
                  :class="{'invisible': activeTestFilter !== testFilters.UNMAPPED_ONLY}"
              >
              </font-awesome-icon>
              Unmapped only
            </b-dropdown-item>
            <b-dropdown-item @click="activeTestFilter = testFilters.MAPPED_ONLY">
              <font-awesome-icon
                  icon="check"
                  class="mr-1"
                  :class="{'invisible': activeTestFilter !== testFilters.MAPPED_ONLY}"
              >
              </font-awesome-icon>
              Mapped only
            </b-dropdown-item>
            <b-dropdown-item @click="activeTestFilter = testFilters.WITH_UPDATES">
              <font-awesome-icon
                  icon="check"
                  class="mr-1"
                  :class="{'invisible': activeTestFilter !== testFilters.WITH_UPDATES}"
              >
              </font-awesome-icon>
              With updates
            </b-dropdown-item>
          </b-dropdown>
          <hr>
        </div>

        <jzd-test-list :tests="filteredTests" :test-mappings-map="testMappingsMap"></jzd-test-list>
      </b-tab>
      <b-tab title="Cycles">

        <div class="mt-3 text-right">
          <button class="btn btn-sm btn-light" @click="refreshCycles()">
            <font-awesome-icon icon="sync" fixed-width class="mr-1" :spin="refreshingTests"></font-awesome-icon>
            Refresh
          </button>
          <hr>
        </div>

        <jzd-cycle-list :cycles="cycles" :versions-map="versionsMap"></jzd-cycle-list>
      </b-tab>
    </b-tabs>

  </div>
</template>

<script>
  import {jiraCyclesApi} from '../../apis/jira/jira-cycles-api';
  import {jiraVersionApi} from '../../apis/jira/jira-version-api';

  export default {
    name: 'jzd-tests-view',
    data() {
      return {
        cycles: [],
        versionsMap: {},
        testFilters: {
          ALL: 0,
          MAPPED_ONLY: 1,
          UNMAPPED_ONLY: 2,
          WITH_UPDATES: 3
        },
        activeTestFilter: null,
        refreshingTests: false,
        refreshingCycles: false
      };
    },
    computed: {
      filteredTests() {
        switch (this.activeTestFilter) {
          case this.testFilters.ALL:
            return this.tests;
          case this.testFilters.MAPPED_ONLY:
            return this.tests.filter(t => this.testMappingsMap[t.id] != null);
          case this.testFilters.UNMAPPED_ONLY:
            return this.tests.filter(t => this.testMappingsMap[t.id] == null);
          case this.testFilters.WITH_UPDATES:
            return this.tests.filter(t => this.testMappingsMap[t.id] != null && this.testMappingsMap[t.id].updates > 0);
          default:
            return this.tests;
        }
      },
      tests() {
        return this.$store.state.jira.tests.tests;
      },
      testMappingsMap() {
        const map = {};
        this.$store.state.testMappings.testMappings.forEach(tm => map[tm.jiraTestId] = tm);
        return map;
      },
      projectId() {
        return this.$store.state.projectMappings.currentProjectMapping.jiraProjectId;
      }
    },
    created() {
      this.activeTestFilter = this.testFilters.ALL;

      jiraVersionApi.find(this.projectId)
        .then(res => res.data.forEach(version => this.versionsMap[version.id] = version))
        .catch(console.error);

      this.loadTests();
      this.loadTestMappings();
      this.loadCycles();
    },
    methods: {

      loadTests() {
        return this.$store.dispatch('jira/tests/load', this.projectId)
          .catch(console.error);
      },

      loadTestMappings() {
        return this.$store.dispatch('testMappings/load', this.projectId)
          .catch(console.error);
      },

      loadCycles() {
        return jiraCyclesApi.find(this.projectId)
          .then(res => this.cycles = res.data)
          .catch(console.error);
      },

      refreshTests() {
        this.refreshingTests = true;
        this.loadTests().then(() => {
          this.loadTestMappings().then(() => {
            this.$toasted.success('Refreshing finished.');
            this.refreshingTests = false;
          });
        });
      },

      refreshCycles() {
        this.refreshingCycles = true;
        this.loadCycles().then(() => {
          this.$toasted.success('Refreshing finished.');
          this.refreshingCycles = false;
        });
      }
    }
  };
</script>

<style scoped>

</style>
