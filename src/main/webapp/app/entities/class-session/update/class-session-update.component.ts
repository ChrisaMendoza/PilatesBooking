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
import { ClassSessionService } from '../service/class-session.service';
import { IClassSession } from '../class-session.model';
import { ClassSessionFormGroup, ClassSessionFormService } from './class-session-form.service';

@Component({
  selector: 'jhi-class-session-update',
  templateUrl: './class-session-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClassSessionUpdateComponent implements OnInit {
  isSaving = false;
  classSession: IClassSession | null = null;

  studiosSharedCollection: IStudio[] = [];
  classTypesSharedCollection: IClassType[] = [];

  protected classSessionService = inject(ClassSessionService);
  protected classSessionFormService = inject(ClassSessionFormService);
  protected studioService = inject(StudioService);
  protected classTypeService = inject(ClassTypeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClassSessionFormGroup = this.classSessionFormService.createClassSessionFormGroup();

  compareStudio = (o1: IStudio | null, o2: IStudio | null): boolean => this.studioService.compareStudio(o1, o2);

  compareClassType = (o1: IClassType | null, o2: IClassType | null): boolean => this.classTypeService.compareClassType(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classSession }) => {
      this.classSession = classSession;
      if (classSession) {
        this.updateForm(classSession);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const classSession = this.classSessionFormService.getClassSession(this.editForm);
    if (classSession.id !== null) {
      this.subscribeToSaveResponse(this.classSessionService.update(classSession));
    } else {
      this.subscribeToSaveResponse(this.classSessionService.create(classSession));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClassSession>>): void {
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

  protected updateForm(classSession: IClassSession): void {
    this.classSession = classSession;
    this.classSessionFormService.resetForm(this.editForm, classSession);

    this.studiosSharedCollection = this.studioService.addStudioToCollectionIfMissing<IStudio>(
      this.studiosSharedCollection,
      classSession.studio,
    );
    this.classTypesSharedCollection = this.classTypeService.addClassTypeToCollectionIfMissing<IClassType>(
      this.classTypesSharedCollection,
      classSession.classType,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studioService
      .query()
      .pipe(map((res: HttpResponse<IStudio[]>) => res.body ?? []))
      .pipe(map((studios: IStudio[]) => this.studioService.addStudioToCollectionIfMissing<IStudio>(studios, this.classSession?.studio)))
      .subscribe((studios: IStudio[]) => (this.studiosSharedCollection = studios));

    this.classTypeService
      .query()
      .pipe(map((res: HttpResponse<IClassType[]>) => res.body ?? []))
      .pipe(
        map((classTypes: IClassType[]) =>
          this.classTypeService.addClassTypeToCollectionIfMissing<IClassType>(classTypes, this.classSession?.classType),
        ),
      )
      .subscribe((classTypes: IClassType[]) => (this.classTypesSharedCollection = classTypes));
  }
}
