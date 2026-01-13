import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IClassType } from '../class-type.model';

@Component({
  selector: 'jhi-class-type-detail',
  templateUrl: './class-type-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ClassTypeDetailComponent {
  classType = input<IClassType | null>(null);

  previousState(): void {
    window.history.back();
  }
}
