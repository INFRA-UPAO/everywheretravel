import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { PagoPaxService } from './pago-pax.service';

describe('PagoPaxService', () => {
  let service: PagoPaxService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PagoPaxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
