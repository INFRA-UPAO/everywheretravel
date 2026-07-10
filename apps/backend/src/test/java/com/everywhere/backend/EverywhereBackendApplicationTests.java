package com.everywhere.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// jwk-set-uri en vez de issuer-uri: no dispara una llamada de red al armar el
// bean JwtDecoder (issuer-uri sí lo hace, resolviendo .well-known/openid-configuration),
// asi el contexto levanta en CI sin depender de un Cognito real.
@SpringBootTest(properties = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://example.com/.well-known/jwks.json")
class EverywhereBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
