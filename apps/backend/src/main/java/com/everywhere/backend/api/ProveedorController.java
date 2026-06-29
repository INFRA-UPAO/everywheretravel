package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ProveedorService; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<List<ProveedorResponseDTO>> findAll() { 
        return ResponseEntity.ok(proveedorService.getAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<ProveedorResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorService.getById(id));
    }

    @PostMapping
    @RequirePermission(module = "PROVEEDORES", permission = "CREATE")
    public ResponseEntity<ProveedorResponseDTO> create(@RequestBody  ProveedorRequestDTO proveedorRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.create(proveedorRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "UPDATE")
    public ResponseEntity<ProveedorResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody ProveedorRequestDTO proveedorRequestDTO) { 
        return ResponseEntity.ok(proveedorService.update(id, proveedorRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
