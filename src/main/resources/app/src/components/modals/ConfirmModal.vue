<template>
  <div>
    <b-modal ref="modal" lazy>
      <p>{{text}}</p>

      <template slot="modal-footer">
        <button class="btn btn-primary" @click="close()">Ok</button>
        <button class="btn btn-outline-secondary" @click="dismiss()">Cancel</button>
      </template>
    </b-modal>
  </div>
</template>

<script>

  /**
   * Component for a simple modal dialog that lets users confirm an action.
   */
  export default {
    name: 'afj-confirm-modal',
    data() {
      return {

        /**
         * The text to display.
         * @type {?string}
         */
        text: null
      };
    },
    methods: {

      /**
       * Open the modal.
       *
       * @param {string} text The text to display.
       */
      open(text) {
        this.text = text;
        this.$refs.modal.show();
      },

      close() {
        this.$refs.modal.hide();
        this.$emit('close');
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
