/*
 * Copyright 2018 Alexander Bainczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Vue from 'vue';
import Router from 'vue-router';
import AppView from './components/views/AppView';
import ProjectView from './components/views/ProjectView';
import ProjectsView from './components/views/ProjectsView';
import SettingsView from './components/views/SettingsView';
import TestsView from './components/views/TestsView';
import EmptyView from './components/views/EmptyView';
import LoginView from './components/views/LoginView';
import AdminUsersView from './components/views/AdminUsersView';
import {projectMappingApi} from './apis/project-mapping-api';
import {store} from './stores/store';

Vue.use(Router);

/** Define application routes. */
export default new Router({
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: 'app'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/app',
      name: 'app',
      component: AppView,
      beforeEnter: beforeEnterApp,
      redirect: 'app/projects',
      children: [
        {
          path: 'settings',
          name: 'settings',
          component: SettingsView
        },
        {
          path: 'projects',
          component: EmptyView,
          children: [
            {
              path: '',
              name: 'projects',
              component: ProjectsView
            },
            {
              path: ':projectId',
              component: EmptyView,
              beforeEnter: beforeEnterProject,
              children: [
                {
                  path: '',
                  name: 'project',
                  component: ProjectView
                },
                {
                  path: 'tests',
                  component: EmptyView,
                  children: [
                    {
                      path: '',
                      name: 'tests',
                      component: TestsView
                    }
                  ]
                }
              ]
            }
          ]
        },
        {
          path: 'admin',
          component: EmptyView,
          beforeEnter: beforeEnterAdmin,
          children: [
            {
              path: '',
              name: 'adminUsers',
              component: AdminUsersView
            }
          ]
        }
      ]
    }
  ]
});

/**
 * Guard that is executed before a project related URL is opened.
 * Makes sure there is the project mapping that is defined as route parameter is in the store.
 */
function beforeEnterProject(to, from, next) {
  if (store.state.projectMappings.currentProjectMapping == null) {
    const projectId = to.params.projectId;
    projectMappingApi.findOneByJiraProjectId(projectId)
      .then(res => {
        store.commit('projectMappings/setCurrent', res.data);
        next();
      })
      .catch(console.error);
  } else {
    next();
  }
}

/**
 * Makes sure a user is logged in before entering any view in the app.
 */
function beforeEnterApp(to, from, next) {
  if (store.state.users.currentUser == null) {
    const user = localStorage.getItem('user');
    if (user == null) {
      next('login');
    } else {
      store.commit('users/setCurrentUser', JSON.parse(user));
      next();
    }
  } else {
    next();
  }
}

/**
 * Makes sure only admins can navigate to admin related views.
 */
function beforeEnterAdmin(to, from, next) {
  const user = store.state.users.currentUser;
  if (user.role === 'ADMIN') {
    next();
  } else {
    next('login');
  }
}
