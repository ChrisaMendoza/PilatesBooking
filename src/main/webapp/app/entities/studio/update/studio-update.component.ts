import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudio } from '../studio.model';
import { StudioService } from '../service/studio.service';
import { StudioFormGroup, StudioFormService } from './studio-form.service';

@Component({
  selector: 'jhi-studio-update',
  templateUrl: './studio-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StudioUpdateComponent implements OnInit {
  isSaving = false;
  studio: IStudio | null = null;

  protected studioService = inject(StudioService);
  protected studioFormService = inject(StudioFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: StudioFormGroup = this.studioFormService.createStudioFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ studio }) => {
      this.studio = studio;
      if (studio) {
        this.updateForm(studio);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const studio = this.studioFormService.getStudio(this.editForm);
    if (studio.id !== null) {
      this.subscribeToSaveResponse(this.studioService.update(studio));
    } else {
      this.subscribeToSaveResponse(this.studioService.create(studio));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStudio>>): void {
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

  protected updateForm(studio: IStudio): void {
    this.studio = studio;
    this.studioFormService.resetForm(this.editForm, studio);
  }
}
