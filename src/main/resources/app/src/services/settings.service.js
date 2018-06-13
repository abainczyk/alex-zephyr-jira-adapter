import {BehaviorSubject} from 'rxjs';

export class SettingsService {

  constructor() {
    this.settings$ = new BehaviorSubject(null);
  }

  setSettings(settings) {
    this.settings$.next(settings);
  }
}

export const settingsService = new SettingsService();
