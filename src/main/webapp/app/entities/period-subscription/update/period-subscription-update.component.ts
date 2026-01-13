import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IPeriodSubscription } from '../period-subscription.model';
import { PeriodSubscriptionService } from '../service/period-subscription.service';
import { PeriodSubscriptionFormGroup, PeriodSubscriptionFormService } from './period-subscription-form.service';

@Component({
  selector: 'jhi-period-subscription-update',
  templateUrl: './period-subscription-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PeriodSubscriptionUpdateComponent implements OnInit {
  isSaving = false;
  periodSubscription: IPeriodSubscription | null = null;

  usersSharedCollection: IUser[] = [];

  protected periodSubscriptionService = inject(PeriodSubscriptionService);
  protected periodSubscriptionFormService = inject(PeriodSubscriptionFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PeriodSubscriptionFormGroup = this.periodSubscriptionFormService.createPeriodSubscriptionFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ periodSubscription }) => {
      this.periodSubscription = periodSubscription;
      if (periodSubscription) {
        this.updateForm(periodSubscription);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const periodSubscription = this.periodSubscriptionFormService.getPeriodSubscription(this.editForm);
    if (periodSubscription.id !== null) {
      this.subscribeToSaveResponse(this.periodSubscriptionService.update(periodSubscription));
    } else {
      this.subscribeToSaveResponse(this.periodSubscriptionService.create(periodSubscription));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPeriodSubscription>>): void {
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

  protected updateForm(periodSubscription: IPeriodSubscription): void {
    this.periodSubscription = periodSubscription;
    this.periodSubscriptionFormService.resetForm(this.editForm, periodSubscription);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, periodSubscription.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.periodSubscription?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
