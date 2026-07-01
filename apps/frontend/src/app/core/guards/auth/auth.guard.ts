import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { map, take } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
    const oidcSecurityService = inject(OidcSecurityService);
    const router = inject(Router);

    return oidcSecurityService.isAuthenticated$.pipe(
        take(1),
        map(result => {
            if (result.isAuthenticated) {
                return true;
            }
            router.navigate(['/auth/login']);
            return false;
        })
    );
};
