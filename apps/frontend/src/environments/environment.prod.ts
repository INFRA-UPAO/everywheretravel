export const environment = {
    production: true,
    baseURL: 'https://dev.everywheretravel.online/api/v1',
    cognito: {
        authority: 'https://cognito-idp.us-east-2.amazonaws.com/us-east-2_99nwoN7wC',
        clientId: '73rq6vt9ng82fbbpdcednk7d6r',
        redirectUri: 'https://dev.everywheretravel.online/callback',
        logoutUri: 'https://dev.everywheretravel.online/logout',
        logoutEndpoint: 'https://everywhere-travel-dev.auth.us-east-2.amazoncognito.com/logout',
        scope: 'openid email profile'
    }
};
