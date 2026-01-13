import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStudio } from 'app/entities/studio/studio.model';
import { StudioService } from 'app/entities/studio/service/studio.service';
import { IClassType } from 'app/entities/class-type/class-type.model';
import { ClassTypeService } from 'app/entities/class-type/service/class-type.service';
import { IClassSession } from '../class-session.model';
import { ClassSessionService } from '../service/class-session.service';
import { ClassSessionFormService } from './class-session-form.service';

import { ClassSessionUpdateComponent } from './class-session-update.component';

describe('ClassSession Management Update Component', () => {
  let comp: ClassSessionUpdateComponent;
  let fixture: ComponentFixture<ClassSessionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let classSessionFormService: ClassSessionFormService;
  let classSessionService: ClassSessionService;
  let studioService: StudioService;
  let classTypeService: ClassTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ClassSessionUpdateComponent],
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
      .overrideTemplate(ClassSessionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClassSessionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    classSessionFormService = TestBed.inject(ClassSessionFormService);
    classSessionService = TestBed.inject(ClassSessionService);
    studioService = TestBed.inject(StudioService);
    classTypeService = TestBed.inject(ClassTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Studio query and add missing value', () => {
      const classSession: IClassSession = { id: 8832 };
      const studio: IStudio = { id: 24793 };
      classSession.studio = studio;

      const studioCollection: IStudio[] = [{ id: 24793 }];
      jest.spyOn(studioService, 'query').mockReturnValue(of(new HttpResponse({ body: studioCollection })));
      const additionalStudios = [studio];
      const expectedCollection: IStudio[] = [...additionalStudios, ...studioCollection];
      jest.spyOn(studioService, 'addStudioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      expect(studioService.query).toHaveBeenCalled();
      expect(studioService.addStudioToCollectionIfMissing).toHaveBeenCalledWith(
        studioCollection,
        ...additionalStudios.map(expect.objectContaining),
      );
      expect(comp.studiosSharedCollection).toEqual(expectedCollection);
    });

    it('should call ClassType query and add missing value', () => {
      const classSession: IClassSession = { id: 8832 };
      const classType: IClassType = { id: 20843 };
      classSession.classType = classType;

      const classTypeCollection: IClassType[] = [{ id: 20843 }];
      jest.spyOn(classTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: classTypeCollection })));
      const additionalClassTypes = [classType];
      const expectedCollection: IClassType[] = [...additionalClassTypes, ...classTypeCollection];
      jest.spyOn(classTypeService, 'addClassTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      expect(classTypeService.query).toHaveBeenCalled();
      expect(classTypeService.addClassTypeToCollectionIfMissing).toHaveBeenCalledWith(
        classTypeCollection,
        ...additionalClassTypes.map(expect.objectContaining),
      );
      expect(comp.classTypesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const classSession: IClassSession = { id: 8832 };
      const studio: IStudio = { id: 24793 };
      classSession.studio = studio;
      const classType: IClassType = { id: 20843 };
      classSession.classType = classType;

      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      expect(comp.studiosSharedCollection).toContainEqual(studio);
      expect(comp.classTypesSharedCollection).toContainEqual(classType);
      expect(comp.classSession).toEqual(classSession);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassSession>>();
      const classSession = { id: 18095 };
      jest.spyOn(classSessionFormService, 'getClassSession').mockReturnValue(classSession);
      jest.spyOn(classSessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classSession }));
      saveSubject.complete();

      // THEN
      expect(classSessionFormService.getClassSession).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(classSessionService.update).toHaveBeenCalledWith(expect.objectContaining(classSession));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassSession>>();
      const classSession = { id: 18095 };
      jest.spyOn(classSessionFormService, 'getClassSession').mockReturnValue({ id: null });
      jest.spyOn(classSessionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classSession: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classSession }));
      saveSubject.complete();

      // THEN
      expect(classSessionFormService.getClassSession).toHaveBeenCalled();
      expect(classSessionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassSession>>();
      const classSession = { id: 18095 };
      jest.spyOn(classSessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classSession });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(classSessionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStudio', () => {
      it('should forward to studioService', () => {
        const entity = { id: 24793 };
        const entity2 = { id: 1489 };
        jest.spyOn(studioService, 'compareStudio');
        comp.compareStudio(entity, entity2);
        expect(studioService.compareStudio).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareClassType', () => {
      it('should forward to classTypeService', () => {
        const entity = { id: 20843 };
        const entity2 = { id: 27871 };
        jest.spyOn(classTypeService, 'compareClassType');
        comp.compareClassType(entity, entity2);
        expect(classTypeService.compareClassType).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
