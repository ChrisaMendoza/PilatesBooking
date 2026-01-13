import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IClassType } from '../class-type.model';
import { ClassTypeService } from '../service/class-type.service';
import { ClassTypeFormGroup, ClassTypeFormService } from './class-type-form.service';

@Component({
  selector: 'jhi-class-type-update',
  templateUrl: './class-type-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClassTypeUpdateComponent implements OnInit {
  isSaving = false;
  classType: IClassType | null = null;

  protected classTypeService = inject(ClassTypeService);
  protected classTypeFormService = inject(ClassTypeFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClassTypeFormGroup = this.classTypeFormService.createClassTypeFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classType }) => {
      this.classType = classType;
      if (classType) {
        this.updateForm(classType);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const classType = this.classTypeFormService.getClassType(this.editForm);
    if (classType.id !== null) {
      this.subscribeToSaveResponse(this.classTypeService.update(classType));
    } else {
      this.subscribeToSaveResponse(this.classTypeService.create(classType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClassType>>): void {
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

  protected updateForm(classType: IClassType): void {
    this.classType = classType;
    this.classTypeFormService.resetForm(this.editForm, classType);
  }
}
