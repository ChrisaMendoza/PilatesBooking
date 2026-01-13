import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PackDetailComponent } from './pack-detail.component';

describe('Pack Management Detail Component', () => {
  let comp: PackDetailComponent;
  let fixture: ComponentFixture<PackDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PackDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./pack-detail.component').then(m => m.PackDetailComponent),
              resolve: { pack: () => of({ id: 22594 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PackDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PackDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load pack on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PackDetailComponent);

      // THEN
      expect(instance.pack()).toEqual(expect.objectContaining({ id: 22594 }));
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
