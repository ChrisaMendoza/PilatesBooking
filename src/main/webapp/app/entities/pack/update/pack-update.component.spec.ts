import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { PackService } from '../service/pack.service';
import { IPack } from '../pack.model';
import { PackFormService } from './pack-form.service';

import { PackUpdateComponent } from './pack-update.component';

describe('Pack Management Update Component', () => {
  let comp: PackUpdateComponent;
  let fixture: ComponentFixture<PackUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let packFormService: PackFormService;
  let packService: PackService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PackUpdateComponent],
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
      .overrideTemplate(PackUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PackUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    packFormService = TestBed.inject(PackFormService);
    packService = TestBed.inject(PackService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const pack: IPack = { id: 28997 };
      const user: IUser = { id: 3944 };
      pack.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pack });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const pack: IPack = { id: 28997 };
      const user: IUser = { id: 3944 };
      pack.user = user;

      activatedRoute.data = of({ pack });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.pack).toEqual(pack);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPack>>();
      const pack = { id: 22594 };
      jest.spyOn(packFormService, 'getPack').mockReturnValue(pack);
      jest.spyOn(packService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pack });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pack }));
      saveSubject.complete();

      // THEN
      expect(packFormService.getPack).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(packService.update).toHaveBeenCalledWith(expect.objectContaining(pack));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPack>>();
      const pack = { id: 22594 };
      jest.spyOn(packFormService, 'getPack').mockReturnValue({ id: null });
      jest.spyOn(packService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pack: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pack }));
      saveSubject.complete();

      // THEN
      expect(packFormService.getPack).toHaveBeenCalled();
      expect(packService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPack>>();
      const pack = { id: 22594 };
      jest.spyOn(packService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pack });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(packService.update).toHaveBeenCalled();
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
