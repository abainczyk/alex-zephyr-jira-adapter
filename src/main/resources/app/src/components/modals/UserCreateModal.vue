<template>
  <b-modal ref="modal" lazy>
    <template slot="modal-header">
      <h5>Add a new user</h5>
    </template>

    <div class="alert alert-danger" v-if="errorMessage != null">
      The user could not be created. {{errorMessage}}
    </div>

    <form @submit.prevent="createUser()">
      <div class="form-group">
        <label>Email</label>
        <input name="email" class="form-control" type="email" required v-model="user.email"/>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input name="password" class="form-control" type="password" required v-model="user.hashedPassword"/>
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
      <button class="btn btn-primary" @click="createUser()">Add</button>
      <button class="btn btn-outline-secondary" @click="dismiss()">Cancel</button>
    </template>
  </b-modal>
</template>

<script>
  export default {
    name: 'afj-user-create-modal',
    data() {
      return {
        errorMessage: null,
        user: {
          email: null,
          hashedPassword: null,
          role: 'DEFAULT'
        },
        roles: [
          {text: 'Default', value: 'DEFAULT'},
          {text: 'Admin', value: 'ADMIN'}
        ]
      };
    },
    methods: {
      open() {
        this.reset();
        this.$refs.modal.show();
      },

      createUser() {
        this.$store.dispatch('users/create', this.user)
          .then(() => {
            this.$toasted.success('The user has been created.');
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
      },

      reset() {
        this.errorMessage = null;
        this.user = {
          email: null,
          hashedPassword: null,
          role: 'DEFAULT'
        };
      }
    }
  };
</script>

<style scoped>

</style>
