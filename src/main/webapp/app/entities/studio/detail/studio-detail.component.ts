import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IStudio } from '../studio.model';

@Component({
  selector: 'jhi-studio-detail',
  templateUrl: './studio-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class StudioDetailComponent {
  studio = input<IStudio | null>(null);

  previousState(): void {
    window.history.back();
  }
}
