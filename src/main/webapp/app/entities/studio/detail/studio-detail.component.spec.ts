import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { StudioDetailComponent } from './studio-detail.component';

describe('Studio Management Detail Component', () => {
  let comp: StudioDetailComponent;
  let fixture: ComponentFixture<StudioDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudioDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./studio-detail.component').then(m => m.StudioDetailComponent),
              resolve: { studio: () => of({ id: 24793 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(StudioDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudioDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load studio on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', StudioDetailComponent);

      // THEN
      expect(instance.studio()).toEqual(expect.objectContaining({ id: 24793 }));
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
