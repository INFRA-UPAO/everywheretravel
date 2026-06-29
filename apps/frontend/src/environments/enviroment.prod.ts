export const environment = {
    production: true,
    baseURL: "https://everywheretravel.online/api/v1",
    cognito: {
        authority: "https://cognito-idp.us-east-2.amazonaws.com/USER_POOL_ID_PLACEHOLDER",
        clientId: "CLIENT_ID_PLACEHOLDER",
        redirectUri: "https://everywheretravel.online/callback",
        logoutUri: "https://everywheretravel.online/logout",
        scope: "openid email profile"
    }
};
