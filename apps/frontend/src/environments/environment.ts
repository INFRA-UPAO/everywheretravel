export const environment = {
    production: false,
    baseURL: 'http://localhost:8080/api/v1',
    cognito: {
        authority: '',
        clientId: '',
        redirectUri: 'http://localhost:4200/callback',
        logoutUri: 'http://localhost:4200/logout',
        scope: 'openid email profile'
    }
};
