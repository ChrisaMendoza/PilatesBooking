import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IPack } from '../pack.model';
import { PackService } from '../service/pack.service';
import { PackFormGroup, PackFormService } from './pack-form.service';

@Component({
  selector: 'jhi-pack-update',
  templateUrl: './pack-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PackUpdateComponent implements OnInit {
  isSaving = false;
  pack: IPack | null = null;

  usersSharedCollection: IUser[] = [];

  protected packService = inject(PackService);
  protected packFormService = inject(PackFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PackFormGroup = this.packFormService.createPackFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pack }) => {
      this.pack = pack;
      if (pack) {
        this.updateForm(pack);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pack = this.packFormService.getPack(this.editForm);
    if (pack.id !== null) {
      this.subscribeToSaveResponse(this.packService.update(pack));
    } else {
      this.subscribeToSaveResponse(this.packService.create(pack));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPack>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(pack: IPack): void {
    this.pack = pack;
    this.packFormService.resetForm(this.editForm, pack);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, pack.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.pack?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
