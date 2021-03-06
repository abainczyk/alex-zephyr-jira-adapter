<template>
  <div>
    <div class="p-1">
      <font-awesome-icon icon="folder" fixed-width></font-awesome-icon>
      {{testSuite.name}}
    </div>
    <div class="pl-4">
      <div v-for="testSuite in testSuites">
        <afj-test-tree
            :test-suite="testSuite"
            :test-mappings-map="testMappingsMap"
            @selected="selectTestCase"
        >
        </afj-test-tree>
      </div>
    </div>
    <div class="pl-4">
      <div
          v-for="testCase in testCases" class="pl-1 pt-1 pb-1 d-flex flex-row"
          :class="{'test-case': testMappingsMap[testCase.id] == null}"
          @click="selectTestCase(testCase)"
      >
        <div class="w-100 pr-1">
          <font-awesome-icon icon="file" fixed-width></font-awesome-icon>
          {{testCase.name}}
        </div>
        <div>
          <span class="badge badge-danger" v-if="testMappingsMap[testCase.id] != null">In use</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

  /**
   * Component that displays all test suites and test cases in ALEX in a tree.
   */
  export default {
    name: 'afj-test-tree',
    props: {

      /** The map (alexTestId -> testMapping) */
      testMappingsMap: {
        type: Object,
        default: () => ({})
      },

      /** The current test suite. */
      testSuite: {
        type: Object,
        default: () => null
      }
    },
    computed: {

      /**
       * Get all test cases in the current test suite, sorted alphabetically.
       *
       * @return {Object[]}
       */
      testCases() {
        return this.testSuite.tests.filter(t => t.type === 'case').sort((a, b) => {
          if (a.name < b.name) return -1;
          if (a.name > b.name) return 1;
          return 0;
        });
      },

      /**
       * Get all test suites in the current test suite, sorted alphabetically.
       *
       * @return {Object[]}
       */
      testSuites() {
        return this.testSuite.tests.filter(t => t.type === 'suite').sort((a, b) => {
          if (a.name < b.name) return -1;
          if (a.name > b.name) return 1;
          return 0;
        });
      }
    },
    methods: {

      /**
       * Select a test case from the tree.
       *
       * @param {Object} testCase The selected test case.
       */
      selectTestCase(testCase) {
        if (this.testMappingsMap[testCase.id] == null) {
          this.$emit('selected', testCase);
        }
      }
    }
  };
</script>

<style scoped lang="scss">
  .test-case {
    &:hover {
      background: #f2f2f2;
      cursor: pointer;
    }
  }
</style>
