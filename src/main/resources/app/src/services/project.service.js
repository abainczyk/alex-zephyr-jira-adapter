import {BehaviorSubject} from 'rxjs';

export class ProjectService {

  constructor() {
    this.currentProjectMapping$ = new BehaviorSubject(null);
  }

  setCurrentProjectMapping(mapping) {
    this.currentProjectMapping$.next(mapping);
  }
}

export const projectService = new ProjectService();
