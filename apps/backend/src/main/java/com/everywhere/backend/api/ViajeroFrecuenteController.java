package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDto;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ViajeroFrecuenteService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/viajeros-frecuentes")
public class ViajeroFrecuenteController {

    private final ViajeroFrecuenteService viajeroFrecuenteService;

    @PostMapping("/{viajeroId}")
    @RequirePermission(module = "VIAJEROS", permission = "CREATE")
    public ResponseEntity<ViajeroFrecuenteResponseDto> crear(
            @PathVariable Integer viajeroId,
            @RequestBody ViajeroFrecuenteRequestDto viajeroFrecuenteRequestDto) {
        return ResponseEntity.ok(viajeroFrecuenteService.crear(viajeroId, viajeroFrecuenteRequestDto));
    }

    @GetMapping
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroFrecuenteResponseDto>> findAll() {
        return ResponseEntity.ok(viajeroFrecuenteService.findAll());
    }
    
    @GetMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<ViajeroFrecuenteResponseDto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(viajeroFrecuenteService.buscarPorId(id));
    }

    @GetMapping("/viajero/{viajeroId}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroFrecuenteResponseDto>> listarPorViajero(@PathVariable Integer viajeroId) {
        return ResponseEntity.ok(viajeroFrecuenteService.listarPorViajero(viajeroId));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "UPDATE")
    public ResponseEntity<ViajeroFrecuenteResponseDto> actualizar(
            @PathVariable Integer id,
            @RequestBody ViajeroFrecuenteRequestDto viajeroFrecuenteRequestDto) {
        return ResponseEntity.ok(viajeroFrecuenteService.actualizar(id, viajeroFrecuenteRequestDto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        viajeroFrecuenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{viajeroId}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroFrecuenteResponseDto>> buscarPorViajeroId(
            @PathVariable Integer viajeroId) {
        return ResponseEntity.ok(viajeroFrecuenteService.buscarPorViajeroId(viajeroId));
    }
}