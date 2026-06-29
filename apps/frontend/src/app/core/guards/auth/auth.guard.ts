import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { map, take } from 'rxjs';
import { CognitoAuthService } from '../../service/cognito/cognito-auth.service';

export const authGuard: CanActivateFn = (route, state) => {
    const oidcSecurityService = inject(OidcSecurityService);
    const cognitoAuthService = inject(CognitoAuthService);

    return oidcSecurityService.isAuthenticated$.pipe(
        take(1),
        map(result => {
            if (result.isAuthenticated) {
                return true;
            }
            // Redirect to Cognito login
            cognitoAuthService.login();
            return false;
        })
    );
};
