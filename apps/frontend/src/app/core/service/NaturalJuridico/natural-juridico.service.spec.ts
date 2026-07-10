/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NaturalJuridicoService } from './natural-juridico.service';

describe('Service: NaturalJuridico', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NaturalJuridicoService, provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([NaturalJuridicoService], (service: NaturalJuridicoService) => {
    expect(service).toBeTruthy();
  }));
});
