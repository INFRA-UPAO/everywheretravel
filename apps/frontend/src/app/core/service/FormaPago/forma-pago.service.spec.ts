import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { FormaPagoService } from './forma-pago.service';

describe('FormaPagoService', () => {
  let service: FormaPagoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(FormaPagoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
