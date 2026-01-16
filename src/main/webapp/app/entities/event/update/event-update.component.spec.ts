import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStudio } from 'app/entities/studio/studio.model';
import { StudioService } from 'app/entities/studio/service/studio.service';
import { IClassType } from 'app/entities/class-type/class-type.model';
import { ClassTypeService } from 'app/entities/class-type/service/class-type.service';
import { IEvent } from '../event.model';
import { EventService } from '../service/event.service';
import { EventFormService } from './event-form.service';

import { EventUpdateComponent } from './event-update.component';

describe('Event Management Update Component', () => {
  let comp: EventUpdateComponent;
  let fixture: ComponentFixture<EventUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventFormService: EventFormService;
  let eventService: EventService;
  let studioService: StudioService;
  let classTypeService: ClassTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [EventUpdateComponent],
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
      .overrideTemplate(EventUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventFormService = TestBed.inject(EventFormService);
    eventService = TestBed.inject(EventService);
    studioService = TestBed.inject(StudioService);
    classTypeService = TestBed.inject(ClassTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Studio query and add missing value', () => {
      const event: IEvent = { id: 3268 };
      const studio: IStudio = { id: 24793 };
      event.studio = studio;

      const studioCollection: IStudio[] = [{ id: 24793 }];
      jest.spyOn(studioService, 'query').mockReturnValue(of(new HttpResponse({ body: studioCollection })));
      const additionalStudios = [studio];
      const expectedCollection: IStudio[] = [...additionalStudios, ...studioCollection];
      jest.spyOn(studioService, 'addStudioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(studioService.query).toHaveBeenCalled();
      expect(studioService.addStudioToCollectionIfMissing).toHaveBeenCalledWith(
        studioCollection,
        ...additionalStudios.map(expect.objectContaining),
      );
      expect(comp.studiosSharedCollection).toEqual(expectedCollection);
    });

    it('should call ClassType query and add missing value', () => {
      const event: IEvent = { id: 3268 };
      const classType: IClassType = { id: 20843 };
      event.classType = classType;

      const classTypeCollection: IClassType[] = [{ id: 20843 }];
      jest.spyOn(classTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: classTypeCollection })));
      const additionalClassTypes = [classType];
      const expectedCollection: IClassType[] = [...additionalClassTypes, ...classTypeCollection];
      jest.spyOn(classTypeService, 'addClassTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(classTypeService.query).toHaveBeenCalled();
      expect(classTypeService.addClassTypeToCollectionIfMissing).toHaveBeenCalledWith(
        classTypeCollection,
        ...additionalClassTypes.map(expect.objectContaining),
      );
      expect(comp.classTypesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const event: IEvent = { id: 3268 };
      const studio: IStudio = { id: 24793 };
      event.studio = studio;
      const classType: IClassType = { id: 20843 };
      event.classType = classType;

      activatedRoute.data = of({ event });
      comp.ngOnInit();

      expect(comp.studiosSharedCollection).toContainEqual(studio);
      expect(comp.classTypesSharedCollection).toContainEqual(classType);
      expect(comp.event).toEqual(event);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvent>>();
      const event = { id: 22576 };
      jest.spyOn(eventFormService, 'getEvent').mockReturnValue(event);
      jest.spyOn(eventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: event }));
      saveSubject.complete();

      // THEN
      expect(eventFormService.getEvent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventService.update).toHaveBeenCalledWith(expect.objectContaining(event));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvent>>();
      const event = { id: 22576 };
      jest.spyOn(eventFormService, 'getEvent').mockReturnValue({ id: null });
      jest.spyOn(eventService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: event }));
      saveSubject.complete();

      // THEN
      expect(eventFormService.getEvent).toHaveBeenCalled();
      expect(eventService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvent>>();
      const event = { id: 22576 };
      jest.spyOn(eventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ event });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventService.update).toHaveBeenCalled();
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
