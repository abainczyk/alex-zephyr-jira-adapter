<template>
  <div>
    <b-modal ref="modal" lazy>
      <p>{{text}}</p>

      <div class="form-group">
        <input type="text" required v-model="input" class="form-control"/>
      </div>

      <template slot="modal-footer">
        <button class="btn btn-primary" @click="close()">Ok</button>
        <button class="btn btn-outline-secondary" @click="dismiss()">Cancel</button>
      </template>
    </b-modal>
  </div>
</template>

<script>

  /**
   * A component for displaying simple modal that prompts user for text input.
   */
  export default {
    name: 'afj-prompt-modal',
    data() {
      return {

        /**
         * The text to display in the header of the modal.
         * @type {string}
         */
        text: '',

        /**
         * The model for the user input.
         * @type {?string}
         */
        input: null
      };
    },
    methods: {

      /**
       * Open the modal dialog.
       *
       * @param {string} text The text that is displayed in the header.
       * @param {?string} input Value that is displayed as default in the user input field.
       */
      open(text, input = null) {
        this.text = text;
        this.input = input;
        this.$refs.modal.show();
      },

      close() {
        this.$refs.modal.hide();
        this.$emit('close', this.input);
      },

      dismiss() {
        this.$refs.modal.hide();
        this.$emit('dismiss');
      }
    }
  };
</script>

<style scoped>
</style>
