package com.everywhere.backend.security;

import com.everywhere.backend.model.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAspect {

    private final AuthorizationService authorizationService;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        String module = requirePermission.module();
        String permission = requirePermission.permission();

        log.info("Verificando permisos para módulo: {} y permiso: {}", module, permission);

        Role currentRole = authorizationService.getCurrentUserRole();
        if (currentRole != null) {
            log.info("Usuario actual tiene rol: {}", currentRole.getName());
            log.info("Módulos disponibles: {}", currentRole.getModulePermissions().keySet());
            log.info("Permisos por módulo: {}", currentRole.getModulePermissions());
        }

        if (!authorizationService.hasPermission(module, permission)) {
            log.warn("Acceso denegado para módulo: {} y permiso: {} con rol: {}",
                    module, permission, currentRole != null ? currentRole.getName() : "NULL");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para realizar esta acción en el módulo: " + module);
        }

        log.info("Acceso permitido para módulo: {} y permiso: {}", module, permission);
        return joinPoint.proceed();
    }
}
