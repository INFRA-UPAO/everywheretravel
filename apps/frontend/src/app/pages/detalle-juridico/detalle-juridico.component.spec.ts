/* tslint:disable:no-unused-variable */
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAuth } from 'angular-auth-oidc-client';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { DetalleJuridicoComponent } from './detalle-juridico.component';

describe('DetalleJuridicoComponent', () => {
  let component: DetalleJuridicoComponent;
  let fixture: ComponentFixture<DetalleJuridicoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ DetalleJuridicoComponent ],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting(), provideAuth({ config: { authority: 'https://example.com', redirectUrl: 'https://example.com/callback', postLogoutRedirectUri: 'https://example.com/logout', clientId: 'test-client-id', scope: 'openid email profile', responseType: 'code' } })]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetalleJuridicoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
