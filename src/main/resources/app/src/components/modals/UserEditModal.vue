<template>
  <b-modal ref="modal" lazy>
    <template slot="modal-header">
      <h5>Edit user <em>{{email}}</em></h5>
    </template>

    <div class="alert alert-danger" v-if="errorMessage != null">
      The user could not be updated. {{errorMessage}}
    </div>

    <form @submit.prevent="updateUser()" v-if="user != null">
      <div class="form-group">
        <label>Email</label>
        <input name="email" class="form-control" type="email" required v-model="user.email"/>
      </div>
      <div class="form-group">
        <label>Role</label>
        <select class="form-control" name="role" v-model="user.role">
          <option v-for="role in roles" :value="role.value">
            {{role.text}}
          </option>
        </select>
      </div>
    </form>

    <template slot="modal-footer">
      <button class="btn btn-primary" @click="updateUser()">Update</button>
      <button class="btn btn-outline-secondary" @click="dismiss()">Cancel</button>
    </template>
  </b-modal>
</template>

<script>
  export default {
    name: 'afj-user-edit-modal',
    data() {
      return {
        email: null,
        user: null,
        errorMessage: null,
        roles: [
          {text: 'Default', value: 'DEFAULT'},
          {text: 'Admin', value: 'ADMIN'}
        ]
      };
    },
    methods: {
      open(user) {
        this.email = user.email;
        this.user = Object.assign({}, user);
        this.$refs.modal.show();
      },

      updateUser() {
        this.errorMessage = null;
        this.$store.dispatch('users/update', this.user)
          .then(() => {
            this.$toasted.success('The user has been updated.');
            this.close();
          })
          .catch(err => {
            this.errorMessage = err.response.data.message;
          });
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
