/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DocumentoCobranzaService } from './DocumentoCobranza.service';

describe('Service: DocumentoCobranza', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DocumentoCobranzaService, provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([DocumentoCobranzaService], (service: DocumentoCobranzaService) => {
    expect(service).toBeTruthy();
  }));
});
