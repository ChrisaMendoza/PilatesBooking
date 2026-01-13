import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPeriodSubscription } from '../period-subscription.model';
import { PeriodSubscriptionService } from '../service/period-subscription.service';

@Component({
  templateUrl: './period-subscription-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PeriodSubscriptionDeleteDialogComponent {
  periodSubscription?: IPeriodSubscription;

  protected periodSubscriptionService = inject(PeriodSubscriptionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.periodSubscriptionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
