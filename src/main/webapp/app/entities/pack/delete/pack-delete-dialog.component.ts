import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPack } from '../pack.model';
import { PackService } from '../service/pack.service';

@Component({
  templateUrl: './pack-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PackDeleteDialogComponent {
  pack?: IPack;

  protected packService = inject(PackService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.packService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
