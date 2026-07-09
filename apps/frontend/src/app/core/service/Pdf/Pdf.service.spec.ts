/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { PdfService } from './Pdf.service';

describe('Service: Pdf', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PdfService, provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('should ...', inject([PdfService], (service: PdfService) => {
    expect(service).toBeTruthy();
  }));
});
