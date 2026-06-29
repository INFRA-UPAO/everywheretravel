import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { map, take } from 'rxjs';

export const authInverseGuard: CanActivateFn = (route, state) => {
    const oidcSecurityService = inject(OidcSecurityService);
    const router = inject(Router);

    return oidcSecurityService.isAuthenticated$.pipe(
        take(1),
        map(result => {
            // Si esta autenticado, redirigir al dashboard (sin importar el rol)
            if (result.isAuthenticated) {
                router.navigate(['/dashboard']);
                return false;
            }
            return true;
        })
    );
};
