package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ProveedorGrupoContactoRequestDTO;
import com.everywhere.backend.model.dto.ProveedorGrupoContactoResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ProveedorGrupoContactoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedor-grupo-contacto")
@RequiredArgsConstructor
public class ProveedorGrupoContactoController {

    private final ProveedorGrupoContactoService service;

    @GetMapping
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<List<ProveedorGrupoContactoResponseDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<ProveedorGrupoContactoResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<List<ProveedorGrupoContactoResponseDTO>> searchByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.findByNombre(nombre));
    }

    @PostMapping
    @RequirePermission(module = "PROVEEDORES", permission = "CREATE")
    public ResponseEntity<ProveedorGrupoContactoResponseDTO> create(
            @Valid @RequestBody ProveedorGrupoContactoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "UPDATE")
    public ResponseEntity<ProveedorGrupoContactoResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody ProveedorGrupoContactoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
