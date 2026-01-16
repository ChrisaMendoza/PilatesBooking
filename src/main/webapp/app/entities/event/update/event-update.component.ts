import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudio } from 'app/entities/studio/studio.model';
import { StudioService } from 'app/entities/studio/service/studio.service';
import { IClassType } from 'app/entities/class-type/class-type.model';
import { ClassTypeService } from 'app/entities/class-type/service/class-type.service';
import { EventService } from '../service/event.service';
import { IEvent } from '../event.model';
import { EventFormGroup, EventFormService } from './event-form.service';

@Component({
  selector: 'jhi-event-update',
  templateUrl: './event-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EventUpdateComponent implements OnInit {
  isSaving = false;
  event: IEvent | null = null;

  studiosSharedCollection: IStudio[] = [];
  classTypesSharedCollection: IClassType[] = [];

  protected eventService = inject(EventService);
  protected eventFormService = inject(EventFormService);
  protected studioService = inject(StudioService);
  protected classTypeService = inject(ClassTypeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EventFormGroup = this.eventFormService.createEventFormGroup();

  compareStudio = (o1: IStudio | null, o2: IStudio | null): boolean => this.studioService.compareStudio(o1, o2);

  compareClassType = (o1: IClassType | null, o2: IClassType | null): boolean => this.classTypeService.compareClassType(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;
      if (event) {
        this.updateForm(event);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const event = this.eventFormService.getEvent(this.editForm);
    if (event.id !== null) {
      this.subscribeToSaveResponse(this.eventService.update(event));
    } else {
      this.subscribeToSaveResponse(this.eventService.create(event));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvent>>): void {
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

  protected updateForm(event: IEvent): void {
    this.event = event;
    this.eventFormService.resetForm(this.editForm, event);

    this.studiosSharedCollection = this.studioService.addStudioToCollectionIfMissing<IStudio>(this.studiosSharedCollection, event.studio);
    this.classTypesSharedCollection = this.classTypeService.addClassTypeToCollectionIfMissing<IClassType>(
      this.classTypesSharedCollection,
      event.classType,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studioService
      .query()
      .pipe(map((res: HttpResponse<IStudio[]>) => res.body ?? []))
      .pipe(map((studios: IStudio[]) => this.studioService.addStudioToCollectionIfMissing<IStudio>(studios, this.event?.studio)))
      .subscribe((studios: IStudio[]) => (this.studiosSharedCollection = studios));

    this.classTypeService
      .query()
      .pipe(map((res: HttpResponse<IClassType[]>) => res.body ?? []))
      .pipe(
        map((classTypes: IClassType[]) =>
          this.classTypeService.addClassTypeToCollectionIfMissing<IClassType>(classTypes, this.event?.classType),
        ),
      )
      .subscribe((classTypes: IClassType[]) => (this.classTypesSharedCollection = classTypes));
  }
}
