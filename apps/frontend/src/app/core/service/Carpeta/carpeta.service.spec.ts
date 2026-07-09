import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { CarpetaService } from './carpeta.service';

describe('CarpetaService', () => {
  let service: CarpetaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(CarpetaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
