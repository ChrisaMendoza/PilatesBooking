import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { StudioService } from '../service/studio.service';
import { IStudio } from '../studio.model';
import { StudioFormService } from './studio-form.service';

import { StudioUpdateComponent } from './studio-update.component';

describe('Studio Management Update Component', () => {
  let comp: StudioUpdateComponent;
  let fixture: ComponentFixture<StudioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let studioFormService: StudioFormService;
  let studioService: StudioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StudioUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(StudioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StudioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    studioFormService = TestBed.inject(StudioFormService);
    studioService = TestBed.inject(StudioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const studio: IStudio = { id: 1489 };

      activatedRoute.data = of({ studio });
      comp.ngOnInit();

      expect(comp.studio).toEqual(studio);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudio>>();
      const studio = { id: 24793 };
      jest.spyOn(studioFormService, 'getStudio').mockReturnValue(studio);
      jest.spyOn(studioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: studio }));
      saveSubject.complete();

      // THEN
      expect(studioFormService.getStudio).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(studioService.update).toHaveBeenCalledWith(expect.objectContaining(studio));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudio>>();
      const studio = { id: 24793 };
      jest.spyOn(studioFormService, 'getStudio').mockReturnValue({ id: null });
      jest.spyOn(studioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studio: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: studio }));
      saveSubject.complete();

      // THEN
      expect(studioFormService.getStudio).toHaveBeenCalled();
      expect(studioService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStudio>>();
      const studio = { id: 24793 };
      jest.spyOn(studioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ studio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(studioService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
