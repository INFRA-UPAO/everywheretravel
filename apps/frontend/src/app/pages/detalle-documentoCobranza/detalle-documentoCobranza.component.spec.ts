/* tslint:disable:no-unused-variable */
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAuth } from 'angular-auth-oidc-client';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { DetalleDocumentoCobranzaComponent } from './detalle-documentoCobranza.component';

describe('DetalleDocumentoCobranzaComponent', () => {
  let component: DetalleDocumentoCobranzaComponent;
  let fixture: ComponentFixture<DetalleDocumentoCobranzaComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ DetalleDocumentoCobranzaComponent ],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting(), provideAuth({ config: { authority: 'https://example.com', redirectUrl: 'https://example.com/callback', postLogoutRedirectUri: 'https://example.com/logout', clientId: 'test-client-id', scope: 'openid email profile', responseType: 'code' } })]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetalleDocumentoCobranzaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
