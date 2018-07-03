<template>
  <div>
    <h5>
      User management
    </h5>
    <p class="text-muted">
      Add, edit and delete user accounts.
    </p>
    <hr>

    <div>
      <button class="btn btn-primary" @click="openCreateUserModal">Add user</button>
    </div>
    <hr>

    <table class="table">
      <thead>
      <tr>
        <th scope="col">ID</th>
        <th scope="col">Email</th>
        <th scope="col">Role</th>
        <th scope="col">&nbsp;</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in users">
        <td style="width: 1px">{{user.id}}</td>
        <td>{{user.email}}</td>
        <td>
          <span class="badge badge-secondary" :class="{'badge-danger': user.role === 'ADMIN'}">{{user.role}}</span>
        </td>
        <td class="text-right">
          <b-dropdown variant="outline-secondary border-0 rounded" size="sm" text="" :right="true" no-caret>
            <template slot="button-content">
              <font-awesome-icon icon="bars"></font-awesome-icon>
            </template>
            <b-dropdown-item @click="openEditUserModal(user)">
              Edit
            </b-dropdown-item>
            <b-dropdown-item @click="resetPassword(user)">
              Reset password
            </b-dropdown-item>
            <b-dropdown-divider></b-dropdown-divider>
            <b-dropdown-item @click="deleteUser(user)">
              Delete
            </b-dropdown-item>
          </b-dropdown>
        </td>
      </tr>
      </tbody>
    </table>

    <afj-user-create-modal ref="userCreateModal"></afj-user-create-modal>
    <afj-user-edit-modal ref="userEditModal"></afj-user-edit-modal>
  </div>
</template>

<script>
  import {userApi} from '../../apis/user-api';

  export default {
    name: 'afj-admin-users-view',
    computed: {
      users() {
        return this.$store.state.users.users;
      },
      currentUser() {
        return this.$store.state.users.currentUser;
      }
    },
    created() {
      this.$store.dispatch('users/load');
    },
    methods: {

      /** Open the user create dialog. */
      openCreateUserModal() {
        this.$refs.userCreateModal.open();
      },

      /**
       * Open the modal to edit a user.
       *
       * @param {Object} user The user to edit.
       */
      openEditUserModal(user) {
        this.$refs.userEditModal.open(user);
      },

      /**
       * Delete a user.
       *
       * @param {Object} user The user to delete.
       */
      deleteUser(user) {
        this.$store.dispatch('users/remove', user.id)
          .then(() => {
            this.$toasted.success(`The user has been deleted.`);
            if (user.id === this.currentUser.id) {
              this.$store.dispatch('users/signOut')
                .then(() => this.$router.push({name: 'login'}));
            }
          })
          .catch(err => {
            this.$toasted.error(`The user could not be deleted. ${err.response.data.message}`);
          });
      },

      /**
       * Update the password of a user.
       *
       * @param {Object} user The user.
       */
      resetPassword(user) {
        const password = window.prompt('Enter a new password');
        if (password != null && password.trim() !== '') {
          userApi.setPassword(user.id, password)
            .then(() => this.$toasted.success('The password has been updated.'))
            .catch(err => this.$toasted.error(`The password could not be updated. ${err.response.data.message}`));
        }
      }
    }
  };
</script>

<style scoped>
</style>
