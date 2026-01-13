import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IStudio } from '../studio.model';
import { StudioService } from '../service/studio.service';

@Component({
  templateUrl: './studio-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class StudioDeleteDialogComponent {
  studio?: IStudio;

  protected studioService = inject(StudioService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.studioService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
