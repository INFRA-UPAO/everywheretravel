package com.everywhere.backend.model.enums;

import java.util.Map;
import java.util.Set;
import static java.util.Map.entry;

public enum Role {

    GERENTE(1, "GERENTE", Map.ofEntries(
            entry("ALL_MODULES", Set.of("READ","CREATE","UPDATE","DELETE"))
    )),
 
    VENTAS(2, "VENTAS", Map.ofEntries(
            entry("CLIENTES", Set.of("CREATE","READ","UPDATE")),
            entry("VIAJEROS", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("VIAJEROS_FREC", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("COTIZACIONES", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("RECIBOS", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("LIQUIDACIONES", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("PRODUCTOS", Set.of("READ")),
            entry("PROVEEDORES", Set.of("READ")),
            entry("OPERADOR", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("CARPETA", Set.of("CREATE","READ","UPDATE","DELETE"))
    )),


    ADMINISTRAR(3, "ADMINISTRAR", Map.ofEntries(
            entry("CLIENTES", Set.of("CREATE","READ","UPDATE")),
            entry("VIAJEROS", Set.of("CREATE","READ","UPDATE")),
            entry("COTIZACIONES", Set.of("READ")),
            entry("RECIBOS", Set.of("READ")),
            entry("LIQUIDACIONES", Set.of("CREATE","READ","UPDATE")),
            entry("PRODUCTOS", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("PROVEEDORES", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("OPERADOR", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("CARPETA", Set.of("CREATE","READ","UPDATE","DELETE"))
    )),


    ADMIN(4, "ADMIN", Map.ofEntries(
            entry("ALL_MODULES", Set.of("READ","CREATE","UPDATE","DELETE"))
    )),

    SISTEMAS(6, "SISTEMAS", Map.ofEntries(
            entry("ALL_MODULES", Set.of("READ","CREATE","UPDATE","DELETE"))
    )),

    OPERACIONES(5, "OPERACIONES", Map.ofEntries(
            entry("CLIENTES", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("VIAJEROS", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("PRODUCTOS", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("PROVEEDORES", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("OPERADOR", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("PERSONAS", Set.of("CREATE","READ","UPDATE","DELETE"))
    )),

    VENTAS_JUNIOR(7, "VENTAS_JUNIOR", Map.ofEntries(
            entry("CLIENTES", Set.of("CREATE","READ")),
            entry("VIAJEROS", Set.of("CREATE","READ")),
            entry("COTIZACIONES", Set.of("CREATE","READ","UPDATE")),
            entry("RECIBOS", Set.of("CREATE","READ","UPDATE")),
            entry("LIQUIDACIONES", Set.of("CREATE","READ","DELETE")),
            entry("CARPETA", Set.of("CREATE","READ")),
            entry("PERSONAS", Set.of("CREATE","READ"))

    )),

    GERENTE_ARGENTINA(8, "GERENTE_ARGENTINA", Map.ofEntries(
            entry("CLIENTES", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("VIAJEROS", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("COTIZACIONES", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("RECIBOS", Set.of("CREATE","READ","UPDATE","DELETE")),
            entry("LIQUIDACIONES", Set.of("READ")),
            entry("PROVEEDORES", Set.of("READ")),
            entry("OPERADOR", Set.of("READ")),
            entry("CARPETA", Set.of("CREATE","READ")),
            entry("PERSONAS", Set.of("CREATE","READ","UPDATE","DELETE"))

    ));

    private final Integer id;
    private final String name;
    private final Map<String, Set<String>> modulePermissions;

    Role(Integer id, String name, Map<String, Set<String>> modulePermissions) {
        this.id = id;
        this.name = name;
        this.modulePermissions = modulePermissions;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Set<String>> getModulePermissions() {
        return modulePermissions;
    }

    // Verifica si un rol tiene un permiso específico sobre un módulo
    public boolean hasPermission(String module, String action) {
        // Admin y Sistemas pueden acceder a todo
        if (modulePermissions.containsKey("ALL_MODULES")) {
            return modulePermissions.get("ALL_MODULES").contains(action);
        }
        return modulePermissions.containsKey(module) && modulePermissions.get(module).contains(action);
    }

    // Método para obtener rol desde el nombre
    public static Role fromName(String name) {
        for (Role role : values()) {
            if (role.getName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role not found for name: " + name);
    }

    public static Role fromId(Integer id) {
        for (Role role : values()) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role not found for id: " + id);
    }
}
