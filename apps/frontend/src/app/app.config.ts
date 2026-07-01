import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAuth } from 'angular-auth-oidc-client';
import { jwtInterceptor } from './core/interceptos/jwt.interceptor';
import { cacheInterceptor } from './core/interceptos/cache.interceptor';
import { environment } from '../environments/environment';

import { LucideAngularModule, RefreshCcw, CircleUserRound } from 'lucide-angular';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor, cacheInterceptor])),
    importProvidersFrom(
      LucideAngularModule.pick({
        RefreshCcw,
        CircleUserRound,
      }),
    ),
    provideAuth({
      config: {
        authority: environment.cognito.authority,
        redirectUrl: environment.cognito.redirectUri,
        postLogoutRedirectUri: environment.cognito.logoutUri,
        clientId: environment.cognito.clientId,
        scope: environment.cognito.scope,
        responseType: 'code',
        silentRenew: true,
        useRefreshToken: true,
        autoUserInfo: false,
      },
    }),
  ],
};
