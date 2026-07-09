import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideAuth } from 'angular-auth-oidc-client';
import { DocumentosComponent } from './documentos.component';

describe('DocumentosComponent', () => {
  let component: DocumentosComponent;
  let fixture: ComponentFixture<DocumentosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentosComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideAuth({ config: { authority: 'https://example.com', redirectUrl: 'https://example.com/callback', postLogoutRedirectUri: 'https://example.com/logout', clientId: 'test-client-id', scope: 'openid email profile', responseType: 'code' } })]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocumentosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});