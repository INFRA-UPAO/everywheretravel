/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { CorreoPersonaService } from './correo-persona.service';

describe('Service: CorreoPersona', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CorreoPersonaService, provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([CorreoPersonaService], (service: CorreoPersonaService) => {
    expect(service).toBeTruthy();
  }));
});
