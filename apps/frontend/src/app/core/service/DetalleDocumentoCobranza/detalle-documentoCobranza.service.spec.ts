/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DetalleDocumentoCobranzaService } from './detalle-documentoCobranza.service';

describe('Service: DetalleDocumentoCobranza', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DetalleDocumentoCobranzaService, provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([DetalleDocumentoCobranzaService], (service: DetalleDocumentoCobranzaService) => {
    expect(service).toBeTruthy();
  }));
});
