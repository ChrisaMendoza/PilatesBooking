import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IPeriodSubscription } from '../period-subscription.model';

@Component({
  selector: 'jhi-period-subscription-detail',
  templateUrl: './period-subscription-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class PeriodSubscriptionDetailComponent {
  periodSubscription = input<IPeriodSubscription | null>(null);

  previousState(): void {
    window.history.back();
  }
}
