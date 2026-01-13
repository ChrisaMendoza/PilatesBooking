import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IClassSession } from '../class-session.model';
import { ClassSessionService } from '../service/class-session.service';

@Component({
  templateUrl: './class-session-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ClassSessionDeleteDialogComponent {
  classSession?: IClassSession;

  protected classSessionService = inject(ClassSessionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.classSessionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
