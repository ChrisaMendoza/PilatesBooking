import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ClassTypeService } from '../service/class-type.service';
import { IClassType } from '../class-type.model';
import { ClassTypeFormService } from './class-type-form.service';

import { ClassTypeUpdateComponent } from './class-type-update.component';

describe('ClassType Management Update Component', () => {
  let comp: ClassTypeUpdateComponent;
  let fixture: ComponentFixture<ClassTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let classTypeFormService: ClassTypeFormService;
  let classTypeService: ClassTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ClassTypeUpdateComponent],
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
      .overrideTemplate(ClassTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClassTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    classTypeFormService = TestBed.inject(ClassTypeFormService);
    classTypeService = TestBed.inject(ClassTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const classType: IClassType = { id: 27871 };

      activatedRoute.data = of({ classType });
      comp.ngOnInit();

      expect(comp.classType).toEqual(classType);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassType>>();
      const classType = { id: 20843 };
      jest.spyOn(classTypeFormService, 'getClassType').mockReturnValue(classType);
      jest.spyOn(classTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classType }));
      saveSubject.complete();

      // THEN
      expect(classTypeFormService.getClassType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(classTypeService.update).toHaveBeenCalledWith(expect.objectContaining(classType));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassType>>();
      const classType = { id: 20843 };
      jest.spyOn(classTypeFormService, 'getClassType').mockReturnValue({ id: null });
      jest.spyOn(classTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classType }));
      saveSubject.complete();

      // THEN
      expect(classTypeFormService.getClassType).toHaveBeenCalled();
      expect(classTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClassType>>();
      const classType = { id: 20843 };
      jest.spyOn(classTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(classTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
