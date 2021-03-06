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

import axios from 'axios';
import Vue from 'vue';
import App from './App.vue';
import router from './router';
import Navigation from './components/Navigation';
import ModuleStatus from './components/Status';
import ProjectMappingModal from './components/modals/ProjectMappingModal';
import TestMappingSetupModal from './components/modals/TestMappingModal';
import TestList from './components/TestList';
import ConfirmModal from './components/modals/ConfirmModal';
import IssueEventList from './components/IssueEventList';
import CycleList from './components/cycle-list/CycleList';
import CycleListItem from './components/cycle-list/CycleListItem';
import CycleExecuteModal from './components/modals/CycleExecuteModal';
import FolderList from './components/cycle-list/folder-list/FolderList';
import FolderListItem from './components/cycle-list/folder-list/FolderListItem';
import TestExecuteModal from './components/modals/TestExecuteModal';
import PromptModal from './components/modals/PromptModal';
import ProjectUrlList from './components/ProjectUrlList';
import ExecutionStatus from './components/ExecutionStatus';
import FolderExecuteModal from './components/modals/FolderExecuteModal';
import TestTree from './components/TestTree';
import UserCreateModal from './components/modals/UserCreateModal';
import UserEditModal from './components/modals/UserEditModal';

import fontawesome from '@fortawesome/fontawesome';
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome';
import {
  faAngleLeft,
  faAngleRight,
  faBars,
  faCaretDown,
  faCaretRight,
  faCheck,
  faCircleNotch,
  faClone,
  faEdit,
  faFile,
  faFilter,
  faFolder,
  faFolderOpen,
  faMinus,
  faPlay,
  faPlus,
  faSearch,
  faSort,
  faSync,
  faTimes,
  faUnlink
} from '@fortawesome/fontawesome-free-solid';

import BootstrapVue from 'bootstrap-vue';
import Toasted from 'vue-toasted';

fontawesome.library.add(faSort, faTimes, faEdit, faAngleLeft, faAngleRight, faCheck, faMinus, faPlus, faCaretRight,
  faCaretDown, faSearch, faUnlink, faSync, faFolder, faFolderOpen, faFile, faBars, faPlay, faClone, faCircleNotch,
  faFilter);

Vue.component('font-awesome-icon', FontAwesomeIcon);

Vue.use(BootstrapVue);
Vue.use(Toasted, {
  position: 'bottom-center',
  duration: 4000,
  singleton: true
});

Vue.config.productionTip = false;

Vue.component(Navigation.name, Navigation);
Vue.component(ModuleStatus.name, ModuleStatus);
Vue.component(TestList.name, TestList);
Vue.component(ProjectMappingModal.name, ProjectMappingModal);
Vue.component(ConfirmModal.name, ConfirmModal);
Vue.component(PromptModal.name, PromptModal);
Vue.component(TestMappingSetupModal.name, TestMappingSetupModal);
Vue.component(IssueEventList.name, IssueEventList);
Vue.component(CycleList.name, CycleList);
Vue.component(CycleListItem.name, CycleListItem);
Vue.component(CycleExecuteModal.name, CycleExecuteModal);
Vue.component(FolderList.name, FolderList);
Vue.component(FolderListItem.name, FolderListItem);
Vue.component(TestExecuteModal.name, TestExecuteModal);
Vue.component(ProjectUrlList.name, ProjectUrlList);
Vue.component(ExecutionStatus.name, ExecutionStatus);
Vue.component(FolderExecuteModal.name, FolderExecuteModal);
Vue.component(TestTree.name, TestTree);
Vue.component(UserCreateModal.name, UserCreateModal);
Vue.component(UserEditModal.name, UserEditModal);

/**
 * Include the "Authorization" token for the HTTP Basic Auth in each request.
 */
axios.interceptors.request.use(config => {
  config.headers['Authorization'] = localStorage.getItem('token');
  return config;
}, error => {
  return Promise.reject(error);
});

new Vue({
  router,
  store: require('./stores/store').store,
  render: h => h(App)
}).$mount('#app');
