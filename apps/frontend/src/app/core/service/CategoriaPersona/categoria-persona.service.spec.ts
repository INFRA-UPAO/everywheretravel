/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { CategoriaPersonaService } from './categoria-persona.service';

describe('Service: CategoriaPersona', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CategoriaPersonaService, provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([CategoriaPersonaService], (service: CategoriaPersonaService) => {
    expect(service).toBeTruthy();
  }));
});
