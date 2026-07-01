export const environment = {
    production: true,
    baseURL: 'https://everywheretravel.online/api/v1',
    cognito: {
        authority: 'https://cognito-idp.us-east-2.amazonaws.com/us-east-2_XV4rxeuAF',
        clientId: '4th6k4f501q9oaobm4rfqu33g5',
        redirectUri: 'https://everywheretravel.online/callback',
        logoutUri: 'https://everywheretravel.online/logout',
        logoutEndpoint: 'https://everywhere-travel-prod.auth.us-east-2.amazoncognito.com/logout',
        scope: 'openid email profile'
    }
};
