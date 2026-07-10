package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.EstadoCotizacionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados-cotizacion")
@RequiredArgsConstructor
public class EstadosCotizacionController {

    private final EstadoCotizacionService estadoCotizacionService;

    @PostMapping
    @RequirePermission(module = "COTIZACIONES", permission = "CREATE")
    public ResponseEntity<EstadoCotizacionResponseDTO> create(@RequestBody EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estadoCotizacionService.create(estadoCotizacionRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "UPDATE")
    public ResponseEntity<EstadoCotizacionResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        return ResponseEntity.ok(estadoCotizacionService.update(id, estadoCotizacionRequestDTO));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<EstadoCotizacionResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(estadoCotizacionService.getById(id));
    }

    @GetMapping
    @RequirePermission(module = "COTIZACIONES", permission = "READ")
    public ResponseEntity<List<EstadoCotizacionResponseDTO>> getAll() {
        return ResponseEntity.ok(estadoCotizacionService.getAll());
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "COTIZACIONES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        estadoCotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}