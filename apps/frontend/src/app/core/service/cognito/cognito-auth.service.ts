import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CognitoAuthService {
  private oidcSecurityService = inject(OidcSecurityService);

  isAuthenticated$: Observable<boolean> = this.oidcSecurityService.isAuthenticated$.pipe(
    map(result => result.isAuthenticated)
  );

  login(): void {
    this.oidcSecurityService.authorize();
  }

  logout(): void {
    this.oidcSecurityService.logoffLocal();
    const logoutUrl = `${environment.cognito.logoutEndpoint}?client_id=${environment.cognito.clientId}&logout_uri=${encodeURIComponent(environment.cognito.logoutUri)}`;
    window.location.href = logoutUrl;
  }

  getAccessToken(): Observable<string> {
    return this.oidcSecurityService.getAccessToken();
  }

  handleCallback(): Observable<boolean> {
    return this.oidcSecurityService.checkAuth().pipe(
      map(result => result.isAuthenticated)
    );
  }

  getUserEmail(): Observable<string | null> {
    return this.oidcSecurityService.userData$.pipe(
      map(userData => userData?.userData?.email ?? null)
    );
  }
}
