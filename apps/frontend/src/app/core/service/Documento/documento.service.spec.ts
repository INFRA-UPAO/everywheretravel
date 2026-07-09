import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { DocumentoService } from './documento.service';

describe('DocumentoService', () => {
  let service: DocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(DocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
