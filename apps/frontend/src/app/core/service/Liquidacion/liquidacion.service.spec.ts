import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { LiquidacionService } from './liquidacion.service';

describe('LiquidacionService', () => {
  let service: LiquidacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(LiquidacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
