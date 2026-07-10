import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { DetalleCotizacionService } from './detalle-cotizacion.service';

describe('DetalleCotizacionService', () => {
  let service: DetalleCotizacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(DetalleCotizacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
