package com.everywhere.backend.config;

import com.everywhere.backend.model.entity.Role;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.RoleRepository;
import com.everywhere.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoUserSeeder implements ApplicationRunner {

    private static final String COGNITO_MANAGED_PASSWORD = "COGNITO_MANAGED";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Value("${app.seed.demo-user.enabled:true}")
    private boolean enabled;

    @Value("${app.seed.demo-user.email:docente@example.com}")
    private String email;

    @Value("${app.seed.demo-user.name:Docente Demo}")
    private String name;

    @Value("${app.seed.demo-user.role:ADMIN}")
    private String roleName;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Seed de usuario demo desactivado");
            return;
        }

        String normalizedEmail = normalize(email);
        String normalizedRoleName = normalize(roleName).toUpperCase();

        if (normalizedEmail.isBlank()) {
            log.warn("Seed de usuario demo omitido porque el email esta vacio");
            return;
        }

        seedRoles();

        Role role = roleRepository.findByNameIgnoreCase(normalizedRoleName)
                .orElseGet(() -> createRole(normalizedRoleName));

        userRepository.findByEmail(normalizedEmail).ifPresentOrElse(
                existingUser -> updateDemoUser(existingUser, role),
                () -> createDemoUser(normalizedEmail, role));
    }

    private void seedRoles() {
        for (com.everywhere.backend.model.enums.Role role : com.everywhere.backend.model.enums.Role.values()) {
            roleRepository.findByNameIgnoreCase(role.getName())
                    .orElseGet(() -> createRole(role.getName()));
        }
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        Role savedRole = roleRepository.save(role);
        log.info("Rol seed creado: {}", savedRole.getName());
        return savedRole;
    }

    private void createDemoUser(String normalizedEmail, Role role) {
        User user = new User();
        user.setNombre(name);
        user.setEmail(normalizedEmail);
        user.setPassword(COGNITO_MANAGED_PASSWORD);
        user.setRole(role);
        userRepository.save(user);
        log.info("Usuario demo creado en BD: {} con rol {}", normalizedEmail, role.getName());
    }

    private void updateDemoUser(User user, Role role) {
        user.setNombre(name);
        user.setPassword(COGNITO_MANAGED_PASSWORD);
        user.setRole(role);
        userRepository.save(user);
        log.info("Usuario demo actualizado en BD: {} con rol {}", user.getEmail(), role.getName());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
