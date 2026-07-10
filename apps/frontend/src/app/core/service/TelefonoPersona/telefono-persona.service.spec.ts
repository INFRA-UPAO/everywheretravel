/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TelefonoPersonaService } from './telefono-persona.service';

describe('Service: TelefonoPersona', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TelefonoPersonaService, provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([TelefonoPersonaService], (service: TelefonoPersonaService) => {
    expect(service).toBeTruthy();
  }));
});
