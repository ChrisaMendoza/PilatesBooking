import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPack } from '../pack.model';

@Component({
  selector: 'jhi-pack-detail',
  templateUrl: './pack-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PackDetailComponent {
  pack = input<IPack | null>(null);

  previousState(): void {
    window.history.back();
  }
}
