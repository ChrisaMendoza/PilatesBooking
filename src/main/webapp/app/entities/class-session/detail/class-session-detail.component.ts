import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IClassSession } from '../class-session.model';

@Component({
  selector: 'jhi-class-session-detail',
  templateUrl: './class-session-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ClassSessionDetailComponent {
  classSession = input<IClassSession | null>(null);

  previousState(): void {
    window.history.back();
  }
}
