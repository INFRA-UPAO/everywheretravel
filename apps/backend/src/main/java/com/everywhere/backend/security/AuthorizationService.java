package com.everywhere.backend.security;

import com.everywhere.backend.exceptions.UnauthorizedAccessException;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.model.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final SecurityContextHelper securityContextHelper;

    public boolean hasPermission(String module, String permission) {
        User user = securityContextHelper.getCurrentUser();

        if (user.getRole() == null) {
            log.warn("Usuario sin rol asignado: {}", user.getEmail());
            throw new UnauthorizedAccessException("El usuario no tiene un rol asignado. Contacte al administrador.");
        }

        String roleName = user.getRole().getName();
        log.info("Verificando permisos para usuario: {} con rol: {}", user.getEmail(), roleName);

        try {
            Role userRole = Role.fromName(roleName);
            boolean hasAccess = userRole.hasPermission(module, permission);

            log.info("Rol {} - Acceso al modulo {} con permiso {}: {}",
                    roleName, module, permission, hasAccess);

            return hasAccess;
        } catch (IllegalArgumentException e) {
            log.error("Rol no encontrado en enum: {} para usuario: {}", roleName, user.getEmail());
            throw new UnauthorizedAccessException("El rol del usuario no es valido. Contacte al administrador.");
        }
    }

    public Role getCurrentUserRole() {
        try {
            User user = securityContextHelper.getCurrentUser();

            if (user.getRole() == null) {
                log.warn("Usuario sin rol asignado: {}", user.getEmail());
                return null;
            }

            return Role.fromName(user.getRole().getName());
        } catch (UnauthorizedAccessException e) {
            log.warn("No hay autenticacion en el contexto: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Error al obtener rol del usuario: {}", e.getMessage());
            return null;
        }
    }

    public boolean isAdminOrSistemas() {
        Role role = getCurrentUserRole();
        return role != null && (role == Role.ADMIN || role == Role.SISTEMAS);
    }
}
