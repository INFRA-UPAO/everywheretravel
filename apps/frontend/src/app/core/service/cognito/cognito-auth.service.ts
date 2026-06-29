import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { OidcSecurityService } from 'angular-auth-oidc-client';

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
    this.oidcSecurityService.logoff().subscribe();
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
