import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PeriodSubscriptionDetailComponent } from './period-subscription-detail.component';

describe('PeriodSubscription Management Detail Component', () => {
  let comp: PeriodSubscriptionDetailComponent;
  let fixture: ComponentFixture<PeriodSubscriptionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PeriodSubscriptionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./period-subscription-detail.component').then(m => m.PeriodSubscriptionDetailComponent),
              resolve: { periodSubscription: () => of({ id: 25656 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PeriodSubscriptionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodSubscriptionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load periodSubscription on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PeriodSubscriptionDetailComponent);

      // THEN
      expect(instance.periodSubscription()).toEqual(expect.objectContaining({ id: 25656 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
