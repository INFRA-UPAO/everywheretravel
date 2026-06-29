package com.everywhere.backend.security;

import com.everywhere.backend.exceptions.UnauthorizedAccessException;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Extrae informacion del usuario autenticado a partir del JWT de Cognito
 * almacenado en el SecurityContext por Spring OAuth2 Resource Server.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextHelper {

    private final UserRepository userRepository;

    /**
     * Obtiene el email del usuario actual desde los claims del JWT de Cognito.
     * Cognito coloca el email en el claim "email" o en "cognito:username".
     */
    public String getCurrentUserEmail() {
        Jwt jwt = getCurrentJwt();

        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            email = jwt.getClaimAsString("cognito:username");
        }
        if (email == null || email.isBlank()) {
            email = jwt.getSubject();
        }

        if (email == null || email.isBlank()) {
            log.error("No se pudo extraer el email del token JWT");
            throw new UnauthorizedAccessException("No se pudo identificar al usuario desde el token.");
        }

        return email;
    }

    /**
     * Busca el usuario en la base de datos usando el email extraido del JWT.
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Usuario no encontrado en BD con email: {}", email);
            return new UnauthorizedAccessException(
                    "Usuario no encontrado en el sistema. Contacte al administrador.");
        });
    }

    /**
     * Extrae el JWT del SecurityContext actual.
     */
    private Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("No se encuentra autenticado. Por favor, inicie sesion.");
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new UnauthorizedAccessException("Tipo de autenticacion no soportado.");
        }

        return jwtAuth.getToken();
    }
}
