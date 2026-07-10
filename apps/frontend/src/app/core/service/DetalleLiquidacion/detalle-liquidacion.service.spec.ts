import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { DetalleLiquidacionService } from './detalle-liquidacion.service';

describe('DetalleLiquidacionService', () => {
  let service: DetalleLiquidacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(DetalleLiquidacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
