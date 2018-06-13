import Vue from 'vue';
import Router from 'vue-router';
import AppView from './components/views/AppView';
import ProjectView from './components/views/ProjectView';
import ProjectsView from './components/views/ProjectsView';
import SettingsView from './components/views/SettingsView';
import TestsView from './components/views/TestsView';
import EmptyView from './components/views/EmptyView';
import {projectService} from './services/project.service';
import {projectMappingApi} from './services/apis/project-mapping-api';

Vue.use(Router);

/**
 * Guard that is executed before a projects related URL is opened.
 * Makes sure that the application settings are available in the app.
 *
 * @param {object} to
 *    The route that is should be navigated to.
 * @param {object} from
 *    The route that is navigated from.
 * @param {function(): *} next
 *    The callback for navigating to the 'to' route.
 */
function beforeEnterProjects(to, from, next) {
  next();
  // if (settingsService.settings$.getValue() == null) {
  //   settingsApi.get()
  //     .then(res => {
  //       settingsService.setSettings(res.data);
  //       next();
  //     });
  // } else {
  //   next();
  // }
}

/**
 * Guard that is executed before a project related URL is opened.
 * Makes sure there is the project mapping that is defined as route parameter is in the store.
 *
 * @param {object} to
 *    The route that is should be navigated to.
 * @param {object} from
 *    The route that is navigated from.
 * @param {function(): *} next
 *    The callback for navigating to the 'to' route.
 */
function beforeEnterProject(to, from, next) {
  const projectId = to.params.projectId;
  if (projectService.currentProjectMapping$.getValue() == null) {
    projectMappingApi.findOne(projectId)
      .then(res => {
        projectService.setCurrentProjectMapping(res.data);
        next();
      })
      .catch(console.error);
  } else {
    next();
  }
}

/** Define application routes. */
export default new Router({
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: 'app'
    },
    {
      path: '/app',
      name: 'app',
      component: AppView,
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
          beforeEnter: beforeEnterProjects,
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
        }
      ]
    }
  ]
});
