export const environment = {
    production: true,
    baseURL: 'https://dev.everywheretravel.online/api/v1',
    cognito: {
        authority: 'https://cognito-idp.us-east-2.amazonaws.com/us-east-2_YT4f0tsly',
        clientId: '4s215qvvta8dlmifqu1ar0gb4i',
        redirectUri: 'https://dev.everywheretravel.online/callback',
        logoutUri: 'https://dev.everywheretravel.online/logout',
        logoutEndpoint: 'https://everywhere-travel-dev.auth.us-east-2.amazoncognito.com/logout',
        scope: 'openid email profile'
    }
};
