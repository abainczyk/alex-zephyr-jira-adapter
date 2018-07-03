<template>
  <div id="login-view" class="d-flex flex-column">
    <afj-status></afj-status>
    <div id="login-wrapper" class="align-self-center m-auto">
      <span id="handle"></span>

      <div class="alert alert-danger mb-3" v-if="errorMessage != null">
        Could not sign in. {{this.errorMessage}}
      </div>

      <div class="card" id="login-mask">
        <div class="card-body">
          <div class="text-center mb-3">
            <h5>ALEX for Jira</h5>
            <hr>
          </div>

          <form @submit.prevent="signIn()">
            <div class="form-group">
              <label>Email</label>
              <input name="email" class="form-control" type="email" required v-model="user.email"/>
            </div>
            <div class="form-group">
              <label>Password</label>
              <input name="password" class="form-control" type="password" required v-model="user.password"/>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Sign in</button>
          </form>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
  export default {
    name: 'afj-login-view',
    data() {
      return {
        user: {
          email: null,
          password: null
        },
        errorMessage: null
      };
    },
    mounted() {
      let offsetY = 0;

      const el = this.$el.querySelector('#login-wrapper');
      const handle = this.$el.querySelector('#handle');
      handle.addEventListener('mousedown', onMousedown);

      function onMouseup() {
        el.style.position = 'relative';
        el.style.top = 0;
        window.removeEventListener('mousemove', onMousemove);
        window.removeEventListener('mouseup', onMouseup);
      }

      function onMousemove(e) {
        el.style.position = 'absolute';
        el.style.top = `${e.clientY - offsetY}px`;
      }

      function onMousedown(e) {
        offsetY = e.offsetY;
        window.addEventListener('mousemove', onMousemove);
        window.addEventListener('mouseup', onMouseup);
      }
    },
    methods: {
      signIn() {
        this.errorMessage = null;
        if (this.user.email == null || this.user.email.trim() === '') {
          this.errorMessage = 'The email must not be empty.';
        } else if (this.user.password == null || this.user.password.trim() === '') {
          this.errorMessage = 'The password must not be empty.';
        } else {
          this.$store.dispatch('users/signIn', this.user)
            .then(() => {
              this.$toasted.success(`You have signed in.`);
              this.$router.push({name: 'projects'});
            })
            .catch(err => {
              this.errorMessage = err.response.data.message;
            });
        }
      }
    }
  };
</script>

<style scoped lang="scss">
  #login-view {
    position: absolute;
    top: 0;
    bottom: 0;
    width: 100%;
    background: #f1f1f1;
  }

  #handle {
    height: 8px;
    width: 8px;
    left: 0;
    position: absolute;
    z-index: 5;

    &:hover {
      cursor: grab;
    }
  }

  #login-wrapper {
    width: 320px;
    position: relative;
  }
</style>
