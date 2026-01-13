import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { PeriodSubscriptionService } from '../service/period-subscription.service';
import { IPeriodSubscription } from '../period-subscription.model';
import { PeriodSubscriptionFormService } from './period-subscription-form.service';

import { PeriodSubscriptionUpdateComponent } from './period-subscription-update.component';

describe('PeriodSubscription Management Update Component', () => {
  let comp: PeriodSubscriptionUpdateComponent;
  let fixture: ComponentFixture<PeriodSubscriptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let periodSubscriptionFormService: PeriodSubscriptionFormService;
  let periodSubscriptionService: PeriodSubscriptionService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PeriodSubscriptionUpdateComponent],
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
      .overrideTemplate(PeriodSubscriptionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PeriodSubscriptionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    periodSubscriptionFormService = TestBed.inject(PeriodSubscriptionFormService);
    periodSubscriptionService = TestBed.inject(PeriodSubscriptionService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const periodSubscription: IPeriodSubscription = { id: 8229 };
      const user: IUser = { id: 3944 };
      periodSubscription.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ periodSubscription });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const periodSubscription: IPeriodSubscription = { id: 8229 };
      const user: IUser = { id: 3944 };
      periodSubscription.user = user;

      activatedRoute.data = of({ periodSubscription });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.periodSubscription).toEqual(periodSubscription);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPeriodSubscription>>();
      const periodSubscription = { id: 25656 };
      jest.spyOn(periodSubscriptionFormService, 'getPeriodSubscription').mockReturnValue(periodSubscription);
      jest.spyOn(periodSubscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodSubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: periodSubscription }));
      saveSubject.complete();

      // THEN
      expect(periodSubscriptionFormService.getPeriodSubscription).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(periodSubscriptionService.update).toHaveBeenCalledWith(expect.objectContaining(periodSubscription));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPeriodSubscription>>();
      const periodSubscription = { id: 25656 };
      jest.spyOn(periodSubscriptionFormService, 'getPeriodSubscription').mockReturnValue({ id: null });
      jest.spyOn(periodSubscriptionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodSubscription: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: periodSubscription }));
      saveSubject.complete();

      // THEN
      expect(periodSubscriptionFormService.getPeriodSubscription).toHaveBeenCalled();
      expect(periodSubscriptionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPeriodSubscription>>();
      const periodSubscription = { id: 25656 };
      jest.spyOn(periodSubscriptionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodSubscription });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(periodSubscriptionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
