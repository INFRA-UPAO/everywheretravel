export const environment = {
    production: true,
    baseURL: 'https://everywheretravel.online/api/v1',
    cognito: {
        authority: 'https://cognito-idp.us-east-2.amazonaws.com/us-east-2_yXFEK7L9i',
        clientId: '4puaja447imgbvfa137aol7rsc',
        redirectUri: 'https://everywheretravel.online/callback',
        logoutUri: 'https://everywheretravel.online/logout',
        logoutEndpoint: 'https://everywhere-travel-prod.auth.us-east-2.amazoncognito.com/logout',
        scope: 'openid email profile'
    }
};
