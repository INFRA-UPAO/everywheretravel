package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @RequirePermission(module = "PRODUCTOS", permission = "CREATE")
    public ResponseEntity<ProductoResponseDTO> create(@RequestBody @Valid ProductoRequestDTO productoResponseDTO) {
        return ResponseEntity.ok(productoService.create(productoResponseDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PRODUCTOS", permission = "UPDATE")
    public ResponseEntity<ProductoResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PRODUCTOS", permission = "READ")
    public ResponseEntity<ProductoResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.getById(id));
    }

    @GetMapping
    @RequirePermission(module = "PRODUCTOS", permission = "READ")
    public ResponseEntity<List<ProductoResponseDTO>> getAll() {
        return ResponseEntity.ok(productoService.getAll());
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PRODUCTOS", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
