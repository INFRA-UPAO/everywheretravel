import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { EstadoCotizacionService } from './estado-cotizacion.service';

describe('EstadoCotizacionService', () => {
  let service: EstadoCotizacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(EstadoCotizacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
