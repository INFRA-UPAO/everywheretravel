import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAuth } from 'angular-auth-oidc-client';

import { ProveedorComponent } from './proveedor.component';

describe('ProveedorComponent', () => {
  let component: ProveedorComponent;
  let fixture: ComponentFixture<ProveedorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProveedorComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideAuth({ config: { authority: 'https://example.com', redirectUrl: 'https://example.com/callback', postLogoutRedirectUri: 'https://example.com/logout', clientId: 'test-client-id', scope: 'openid email profile', responseType: 'code' } })]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProveedorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
