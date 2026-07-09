import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ObservacionLiquidacionService } from './observacion-liquidacion';

describe('ObservacionLiquidacionService', () => {
  let service: ObservacionLiquidacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ObservacionLiquidacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
