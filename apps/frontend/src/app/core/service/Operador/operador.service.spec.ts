import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { OperadorService } from './operador.service';

describe('OperadorService', () => {
  let service: OperadorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(OperadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
